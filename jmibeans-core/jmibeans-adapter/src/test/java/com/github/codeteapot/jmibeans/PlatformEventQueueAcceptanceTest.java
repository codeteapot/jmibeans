package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.lang.Thread.currentThread;
import static java.util.logging.Level.SEVERE;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.logging.Handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.platform.event.PlatformListener;
import com.github.codeteapot.testing.logging.LoggerStub;

@ExtendWith(MockitoExtension.class)
class PlatformEventQueueAcceptanceTest {

  private static final MachineAvailableEvent SOME_MACHINE_AVAILABLE_EVENT =
      new MachineAvailableEvent(new Object(), new MachineRef(new byte[0], new byte[0]));
  private static final MachineLostEvent SOME_MACHINE_LOST_EVENT =
      new MachineLostEvent(new Object(), new MachineRef(new byte[0], new byte[0]));

  private static final RuntimeException SOME_DISPATCH_EXCEPTION = new RuntimeException();

  private LoggerStub loggerStub;

  @Mock
  private Handler loggerHandler;

  private PlatformEventQueue eventQueue;

  @BeforeEach
  void setUp() {
    loggerStub = loggerStubFor(PlatformEventQueueDispatch.class.getName(), loggerHandler);
    eventQueue = new PlatformEventQueue();
  }

  @AfterEach
  void tearDown() {
    loggerStub.restore();
  }

  @Test
  void dispatchMachineAvailableEvent(@Mock PlatformListener someListener) {
    eventQueue.addListener(someListener);

    Thread t = new Thread(() -> {
      try {
        eventQueue.dispatchEvents();
      } catch (InterruptedException e) {
        currentThread().interrupt();
      }
    });
    t.start();

    eventQueue.fireAvailableEvent(SOME_MACHINE_AVAILABLE_EVENT);

    await().untilAsserted(() -> {
      verify(someListener).machineAvailable(SOME_MACHINE_AVAILABLE_EVENT);
    });

    t.interrupt();
  }

  @Test
  void dispatchMachineLostEvent(@Mock PlatformListener someListener) {
    eventQueue.addListener(someListener);

    Thread t = new Thread(() -> {
      try {
        eventQueue.dispatchEvents();
      } catch (InterruptedException e) {
        currentThread().interrupt();
      }
    });
    t.start();

    eventQueue.fireLostEvent(SOME_MACHINE_LOST_EVENT);

    await().untilAsserted(() -> {
      verify(someListener).machineLost(SOME_MACHINE_LOST_EVENT);
    });

    t.interrupt();
  }

  @Test
  void logSevereOnDispatchError(@Mock PlatformListener someListener) {
    doThrow(SOME_DISPATCH_EXCEPTION)
        .when(someListener).machineAvailable(SOME_MACHINE_AVAILABLE_EVENT);

    eventQueue.addListener(someListener);

    Thread t = new Thread(() -> {
      try {
        eventQueue.dispatchEvents();
      } catch (InterruptedException e) {
        currentThread().interrupt();
      }
    });
    t.start();

    eventQueue.fireAvailableEvent(SOME_MACHINE_AVAILABLE_EVENT);

    await().untilAsserted(() -> {
      verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(SEVERE) &&
          record.getThrown().equals(SOME_DISPATCH_EXCEPTION)));
    });

    t.interrupt();
  }
}
