package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;

@ExtendWith(MockitoExtension.class)
class MachineShellClientConnectionBridgeFactoryTest {

  private MachineShellClientConnectionBridgeFactory factory;

  @Mock
  private ScheduledExecutorService closeExecutor;

  @Mock
  private Supplier<Duration> idleTimeoutSupplier;

  @Mock
  private MachineShellClientConnectionBridgeConstructor bridgeConstructor;

  @BeforeEach
  void setUp() {
    factory = new MachineShellClientConnectionBridgeFactory(
        closeExecutor,
        idleTimeoutSupplier,
        bridgeConstructor);
  }

  @Test
  void getBridge(
      @Mock MachineShellClientConnection someConnection,
      @Mock MachineShellClientConnectionBridge someBridge) {
    when(bridgeConstructor.construct(closeExecutor, idleTimeoutSupplier, someConnection))
        .thenReturn(someBridge);

    MachineShellClientConnectionBridge bridge = factory.getBridge(someConnection);

    assertThat(bridge).isEqualTo(someBridge);
  }
}
