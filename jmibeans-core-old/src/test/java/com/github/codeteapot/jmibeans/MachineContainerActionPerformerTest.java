package com.github.codeteapot.jmibeans;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.MachineContainerAction;
import com.github.codeteapot.jmibeans.MachineContainerActionPerformer;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MachineContainerActionPerformerTest {

  private static final MachineRef ANY_REF = new MachineRef(new byte[0], new byte[0]);

  @Mock
  private ThreadFactory threadFactory;

  private MachineContainerActionPerformer actionPerformer;

  @BeforeEach
  public void setUp() {
    actionPerformer = new MachineContainerActionPerformer(newFixedThreadPool(1, threadFactory));
  }

  @Test
  public void performCompletely(@Mock MachineContainerAction someAction) {
    when(threadFactory.newThread(any()))
        .thenAnswer(invocation -> new Thread((Runnable) invocation.getArgument(0)));

    actionPerformer.perform(ANY_REF, someAction);

    await().untilAsserted(() -> verify(someAction).perform());
  }

  @Test
  public void performInterrupted(@Mock MachineContainerAction someAction) throws Exception {
    AtomicReference<Thread> t = new AtomicReference<>();
    when(threadFactory.newThread(any()))
        .thenAnswer(invocation -> t.updateAndGet(
            current -> spy(new Thread((Runnable) invocation.getArgument(0)))));
    doThrow(new InterruptedException())
        .when(someAction).perform();
    actionPerformer.perform(ANY_REF, someAction);

    await().untilAsserted(() -> {
      verify(t.get()).interrupt();
    });
  }
}
