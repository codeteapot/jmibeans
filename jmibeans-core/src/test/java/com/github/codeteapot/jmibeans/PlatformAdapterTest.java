package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.MachineCatalog;
import com.github.codeteapot.jmibeans.MachineContainer;
import com.github.codeteapot.jmibeans.MachineContainerConstructor;
import com.github.codeteapot.jmibeans.PlatformAdapter;
import com.github.codeteapot.jmibeans.PlatformContext;
import com.github.codeteapot.jmibeans.PlatformEventSource;
import com.github.codeteapot.jmibeans.PlatformPortId;
import com.github.codeteapot.jmibeans.PlatformPortIdGenerator;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineId;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlatformAdapterTest {

  private static final MachineId SOME_MACHINE_ID = new MachineId(new byte[0]);
  private static final MachineRef SOME_MACHINE_REF = new MachineRef(new byte[0], new byte[0]);

  @Mock
  private PlatformPortIdGenerator portIdGenerator;

  @Mock
  private MachineContainer container;

  private PlatformAdapter adapter;

  @BeforeEach
  public void setUp(
      @Mock PlatformEventSource eventSource,
      @Mock MachineCatalog catalog,
      @Mock MachineSessionFactory sessionFactory,
      @Mock MachineContainerConstructor containerConstructor) {
    when(containerConstructor.construct(eventSource, catalog, sessionFactory))
        .thenReturn(container);
    adapter = new PlatformAdapter(
        eventSource,
        catalog,
        sessionFactory,
        portIdGenerator,
        containerConstructor);
  }

  @Test
  public void machineContainerAsPlatformContext() {
    PlatformContext context = adapter.getContext();

    assertThat(context).isEqualTo(container);
  }

  @Test
  public void listenAcceptAndForget(
      @Mock PlatformPort somePort,
      @Mock MachineLink someLink,
      @Mock PlatformPortId somePortId) throws Exception {
    doAnswer(invocation -> {
      MachineManager manager = (MachineManager) invocation.getArgument(0);
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
