package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnectionFactory;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;

@ExtendWith(MockitoExtension.class)
class PoolingMachineShellConnectionFactoryTest {

  private static final String SOME_USERNAME = "scott";

  private static final boolean POOLED_AVAILABLE = true;
  private static final boolean POOLED_UNAVAILABLE = false;

  private static final MachineShellClientException SOME_SHELL_CLIENT_EXCEPTION =
      new MachineShellClientException(new Exception());

  @Mock
  private MachineShellClientConnectionFactory clientConnectionFactory;

  @Mock
  private MachineShellClientConnectionBridgeFactory clientConnectionBridgeFactory;

  private Collection<PooledMachineShellClientConnection> connections;

  @Mock
  private PooledMachineShellClientConnectionConstructor pooledConnectionConstructor;

  private PoolingMachineShellConnectionFactory connectionFactory;

  @BeforeEach
  void setUp(
      @Mock ScheduledExecutorService closeExecutor,
      @Mock MachineShellClientConnectionBridgeFactoryConstructor bridgeFactoryConstructor) {
    when(bridgeFactoryConstructor.construct(eq(closeExecutor), any()))
        .thenReturn(clientConnectionBridgeFactory);
    connections = new Vector<>();
    connectionFactory = new PoolingMachineShellConnectionFactory(
        clientConnectionFactory,
        connections,
        closeExecutor,
        bridgeFactoryConstructor,
        pooledConnectionConstructor);
  }

  @Test
  void getNewConnection(
      @Mock MachineShellConnection someConnection,
      @Mock MachineShellClientConnection someManaged,
      @Mock MachineShellClientConnectionBridge someBridge,
      @Mock PooledMachineShellClientConnection someUnavailablePooled,
      @Mock PooledMachineShellClientConnection someNewPooled) throws Exception {
    when(clientConnectionFactory.getConnection(SOME_USERNAME))
        .thenReturn(someManaged);
    when(clientConnectionBridgeFactory.getBridge(someManaged))
        .thenReturn(someBridge);
    when(pooledConnectionConstructor.construct(eq(someBridge), eq(SOME_USERNAME), any()))
        .thenReturn(someNewPooled);
    when(someUnavailablePooled.request(SOME_USERNAME))
        .thenReturn(POOLED_UNAVAILABLE);
    when(someNewPooled.getConnection())
        .thenReturn(someConnection);
    connections.add(someUnavailablePooled);

    MachineShellConnection connection = connectionFactory.getConnection(SOME_USERNAME);

    assertThat(connection).isEqualTo(someConnection);
    verify(someManaged).addConnectionEventListener(someNewPooled);
  }

  @Test
  void getAvailableConnection(
      @Mock MachineShellConnection someConnection,
      @Mock PooledMachineShellClientConnection someAvailablePooled) throws Exception {
    when(someAvailablePooled.request(SOME_USERNAME))
        .thenReturn(POOLED_AVAILABLE);
    when(someAvailablePooled.getConnection())
        .thenReturn(someConnection);
    connections.add(someAvailablePooled);

    MachineShellConnection connection = connectionFactory.getConnection(SOME_USERNAME);

    assertThat(connection).isEqualTo(someConnection);
    verify(someAvailablePooled).acquire();
  }

  @Test
  void failWhenErrorOccurredOnGettingNewConnection(
      @Mock PooledMachineShellClientConnection someUnavailablePooled) throws Exception {
    when(clientConnectionFactory.getConnection(SOME_USERNAME))
        .thenThrow(SOME_SHELL_CLIENT_EXCEPTION);
    connections.add(someUnavailablePooled);

    Throwable e = catchThrowable(() -> connectionFactory.getConnection(SOME_USERNAME));

    assertThat(e)
        .isInstanceOf(MachineShellException.class)
        .hasCause(SOME_SHELL_CLIENT_EXCEPTION);
  }

  @Test
  void cleanupCloseAllPooledConnections(@Mock PooledMachineShellClientConnection somePooled) {
    connections.add(somePooled);

    connectionFactory.cleanup();

    verify(somePooled).closeNow();
  }
}
