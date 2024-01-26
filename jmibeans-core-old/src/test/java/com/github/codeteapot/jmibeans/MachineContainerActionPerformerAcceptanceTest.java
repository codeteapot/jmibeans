package com.github.codeteapot.jmibeans;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.github.codeteapot.jmibeans.MachineContainerActionPerformer;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MachineContainerActionPerformerAcceptanceTest {

  private static final MachineRef SOME_MACHINE_REF = new MachineRef(
      new byte[] {0x0a},
      new byte[] {0x01});
  private static final MachineRef ANOTHER_MACHINE_REF = new MachineRef(
      new byte[] {0x0c},
      new byte[] {0x03});

  private MachineContainerActionPerformer actionPerformer;

  @BeforeEach
  public void setUp() {
    actionPerformer = new MachineContainerActionPerformer();
  }

  @Test
  public void serializeByMachineRef(
      @Mock Runnable someFirstTaskBegin,
      @Mock Runnable someFirstTaskEnd,
      @Mock Runnable someSecondTask,
      @Mock Runnable anotherTask) {
    Semaphore someFirstTaskSem = new Semaphore(0);

    actionPerformer.perform(SOME_MACHINE_REF, () -> {
      someFirstTaskBegin.run();
      someFirstTaskSem.acquire();
      someFirstTaskEnd.run();
    });
    
    await().untilAsserted(() -> {
      verify(someFirstTaskBegin).run();
      verify(someFirstTaskEnd, never()).run();
    });
    
    actionPerformer.perform(SOME_MACHINE_REF, someSecondTask::run);
    actionPerformer.perform(ANOTHER_MACHINE_REF, anotherTask::run);

    await().untilAsserted(() -> {
      verify(someFirstTaskEnd, never()).run();
      verify(someSecondTask, never()).run();
      verify(anotherTask).run();
    });

    someFirstTaskSem.release();

    await().untilAsserted(() -> {
      InOrder order = inOrder(someFirstTaskEnd, someSecondTask);
      order.verify(someFirstTaskEnd).run();
      order.verify(someSecondTask).run();
    });
  }
}
