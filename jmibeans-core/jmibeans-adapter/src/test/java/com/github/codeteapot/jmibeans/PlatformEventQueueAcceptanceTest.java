package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.hamcrest.SomeLogRecordMatcher.someLogRecord;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Level.SEVERE;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.platform.event.PlatformListener;
import com.github.codeteapot.testing.logging.junit.jupiter.LoggingExtension;
import com.github.codeteapot.testing.logging.mockito.MockLogger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.logging.Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
class PlatformEventQueueAcceptanceTest {

  private static final MachineAvailableEvent SOME_MACHINE_AVAILABLE_EVENT =
      new MachineAvailableEvent(new Object(), new MachineRef(new byte[0], new byte[0]));
  private static final MachineLostEvent SOME_MACHINE_LOST_EVENT =
      new MachineLostEvent(new Object(), new MachineRef(new byte[0], new byte[0]));

  private static final RuntimeException SOME_DISPATCH_EXCEPTION = new RuntimeException();

  @MockLogger(name = "com.github.codeteapot.jmibeans.PlatformEventQueue")
  private Handler loggerHandler;

  private ExecutorService dispatchExecutor;

  private PlatformEventQueue eventQueue;

  @BeforeEach
  void setUp() {
    dispatchExecutor = newCachedThreadPool();
    eventQueue = new PlatformEventQueue();
  }

  @Test
  void dispatchMachineAvailableEvent(@Mock PlatformListener someListener) {
    eventQueue.addListener(someListener);

    dispatchExecutor.submit(() -> {
      eventQueue.dispatchEvents();
      return null;
    });

    eventQueue.fireAvailable(SOME_MACHINE_AVAILABLE_EVENT);

    await().untilAsserted(() -> verify(someListener)
        .machineAvailable(SOME_MACHINE_AVAILABLE_EVENT));
  }

  @Test
  void dispatchMachineLostEvent(@Mock PlatformListener someListener) {
    eventQueue.addListener(someListener);

    dispatchExecutor.submit(() -> {
      eventQueue.dispatchEvents();
      return null;
    });

    eventQueue.fireLost(SOME_MACHINE_LOST_EVENT);

    await().untilAsserted(() -> verify(someListener).machineLost(SOME_MACHINE_LOST_EVENT));
  }

  @Test
  void dispatchEventOnlyOnceEvenManyDispatcherThreads(
      @Mock PlatformListener someListener,
      @Mock Runnable availableBeginAction,
      @Mock Runnable availableEndAction,
      @Mock Runnable lostAction) {
    Semaphore availableActionBarrier = new Semaphore(0);

    doAnswer(invocation -> {
      availableBeginAction.run();
      availableActionBarrier.acquire();
      availableEndAction.run();
      return null;
    }).when(someListener).machineAvailable(SOME_MACHINE_AVAILABLE_EVENT);
    doAnswer(invocation -> {
      lostAction.run();
      return null;
    }).when(someListener).machineLost(SOME_MACHINE_LOST_EVENT);

    eventQueue.addListener(someListener);

    dispatchExecutor.submit(() -> {
      eventQueue.dispatchEvents();
      return null;
    });
    dispatchExecutor.submit(() -> {
      eventQueue.dispatchEvents();
      return null;
    });

    eventQueue.fireAvailable(SOME_MACHINE_AVAILABLE_EVENT);
    eventQueue.fireLost(SOME_MACHINE_LOST_EVENT);

    await().untilAsserted(() -> {
      verify(availableBeginAction, times(1)).run();
      verify(availableEndAction, never()).run();
      verify(lostAction, times(1)).run();
    });

    availableActionBarrier.release();

    await().untilAsserted(() -> {
      verify(availableBeginAction, times(1)).run();
      verify(availableEndAction, times(1)).run();
      verify(lostAction, times(1)).run();
    });
  }

  @Test
  void logSevereOnDispatchError(@Mock PlatformListener someListener) {
    doThrow(SOME_DISPATCH_EXCEPTION)
        .when(someListener).machineAvailable(SOME_MACHINE_AVAILABLE_EVENT);

    eventQueue.addListener(someListener);

    dispatchExecutor.submit(() -> {
      eventQueue.dispatchEvents();
      return null;
    });

    eventQueue.fireAvailable(SOME_MACHINE_AVAILABLE_EVENT);

    await().untilAsserted(() -> verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(SEVERE))
        .withThrown(equalTo(SOME_DISPATCH_EXCEPTION)))));
  }
}
