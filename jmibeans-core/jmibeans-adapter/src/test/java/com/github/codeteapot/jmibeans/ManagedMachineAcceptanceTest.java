package com.github.codeteapot.jmibeans;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineBuilderPropertyResolver;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.testing.logging.junit.jupiter.LoggingExtension;
import com.github.codeteapot.testing.logging.mockito.MockLogger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
class ManagedMachineAcceptanceTest {

  private static final MachineRef ANY_REF = new MachineRef(new byte[0], new byte[0]);

  @Test
  void disposeAfterBuildSuccess(
      @Mock PlatformEventTarget someEventTarget,
      @Mock MachineBuilderPropertyResolver anBuilderPropertyResolver,
      @Mock MachineAgent anyAgent,
      @Mock MachineBuilder someBuilder) throws Exception {
    Semaphore buildingStart = new Semaphore(0);
    AtomicBoolean buildingEnd = new AtomicBoolean(false);
    doAnswer(invocation -> {
      buildingStart.release();
      while (!buildingEnd.get()); // Cannot use semaphore since acquire is interruptible
      return null;
    }).when(someBuilder).build(any());

    ManagedMachine machine = new ManagedMachine(
        ANY_REF,
        someEventTarget,
        anBuilderPropertyResolver,
        anyAgent,
        new ManagedMachineBuildingJob(newFixedThreadPool(1), someBuilder));

    buildingStart.acquire();
    machine.dispose();
    buildingEnd.set(true);

    await().untilAsserted(() -> verify(someBuilder).build(any()));
    verify(someEventTarget, never()).fireAvailable(any());
  }

  @Test
  void disposeAfterBuildFailure(
      @Mock PlatformEventTarget someEventTarget,
      @Mock MachineBuilderPropertyResolver anyBuilderPropertyResolver,
      @Mock MachineAgent anyAgent,
      @Mock MachineBuilder someBuilder,
      @MockLogger(name = "com.github.codeteapot.jmibeans.ManagedMachine") Handler loggerHandler)
      throws Exception {
    Semaphore buildingStart = new Semaphore(0);
    AtomicBoolean buildingEnd = new AtomicBoolean(false);
    doAnswer(invocation -> {
      buildingStart.release();
      while (!buildingEnd.get()); // Cannot use semaphore since acquire is interruptible
      throw new MachineBuildingException("Any message");
    }).when(someBuilder).build(any());

    ManagedMachine machine = new ManagedMachine(
        ANY_REF,
        someEventTarget,
        anyBuilderPropertyResolver,
        anyAgent,
        new ManagedMachineBuildingJob(newFixedThreadPool(1), someBuilder));

    buildingStart.acquire();
    machine.dispose();
    buildingEnd.set(true);

    await().untilAsserted(() -> verify(someBuilder).build(any()));
    verify(loggerHandler, never()).publish(any());
  }
}
