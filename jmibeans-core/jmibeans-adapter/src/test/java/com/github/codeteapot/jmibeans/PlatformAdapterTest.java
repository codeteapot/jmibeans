package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.PlatformPort;

@ExtendWith(MockitoExtension.class)
class PlatformAdapterTest {

  private static final byte[] SOME_MACHINE_ID = {1, 2, 3};
  private static final MachineRef SOME_MACHINE_REF = new MachineRef(
      new byte[] {1, 2, 3},
      new byte[] {4, 5, 6});

  @Mock
  private PlatformPortIdGenerator portIdGenerator;

  @Mock
  private MachineContainer container;

  private PlatformAdapter adapter;

  @BeforeEach
  void setUp(
      @Mock PlatformEventTarget eventTarget,
      @Mock MachineCatalog catalog,
      @Mock ExecutorService builderExecutor,
      @Mock MachineContainerConstructor containerConstructor) {
    when(containerConstructor.construct(eventTarget, catalog, builderExecutor))
        .thenReturn(container);
    adapter = new PlatformAdapter(
        eventTarget,
        catalog,
        builderExecutor,
        portIdGenerator,
        containerConstructor);
  }

  @Test
  void machineContainerAsPlatformContext() {
    PlatformContext context = adapter.getContext();

    assertThat(context).isEqualTo(container);
  }

  @Test
  void listenAcceptAndForget(
      @Mock PlatformPort somePort,
      @Mock MachineLink someLink,
      @Mock PlatformPortId somePortId) throws Exception {
    doAnswer(invocation -> {
      MachineManager manager = invocation.getArgument(0);
      manager.accept(SOME_MACHINE_ID, someLink);
      manager.forget(SOME_MACHINE_ID);
      return null;
    }).when(somePort).listen(any());
    when(portIdGenerator.generate())
        .thenReturn(somePortId);
    when(somePortId.machineRef(SOME_MACHINE_ID))
        .thenReturn(SOME_MACHINE_REF);

    adapter.listen(somePort);

    verify(container).accept(SOME_MACHINE_REF, someLink);
  }
}