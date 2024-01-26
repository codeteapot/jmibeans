package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnectionEvent;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

@ExtendWith(MockitoExtension.class)
class PooledMachineShellConnectionTest {

  private static final boolean AVAILABLE = true;
  private static final boolean UNAVAILABLE = false;

  private static final String SOME_USERNAME = "scott";
  private static final String SOME_FILE_PATH = "/some/file";

  private static final Object SOME_EXECUTION_RESULT = new Object();

  private static final MachineShellClientException SOME_CLIENT_EXCEPTION =
      new MachineShellClientException(new Exception());

  @Mock
  private Consumer<PooledMachineShellClientConnection> removeAction;

  @Mock
  private MachineShellConnectionConstructor connectionConstructor;

  @Mock
  private PooledMachineShellClientConnectionState state;

  private PooledMachineShellClientConnection connection;

  @BeforeEach
  void setUp(@Mock PooledMachineShellClientConnectionStateMapper stateMapper) {
    when(stateMapper.map(any())).thenReturn(state);
    connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        stateMapper);
  }

  @Test
  void connectionClosedWithoutException(@Mock MachineShellClientConnection someManaged) {
    connection.connectionClosed(new MachineShellClientConnectionEvent(someManaged));

    verify(state).onClosed();
    verify(removeAction).accept(connection);
    verify(someManaged).removeConnectionEventListener(connection);
  }

  @Test
  void connectionClosedWithException(@Mock MachineShellClientConnection someManaged) {
    connection.connectionClosed(new MachineShellClientConnectionEvent(
        someManaged,
        SOME_CLIENT_EXCEPTION));

    verify(state).onClosed(SOME_CLIENT_EXCEPTION);
    verify(removeAction).accept(connection);
    verify(someManaged).removeConnectionEventListener(connection);
  }

  @Test
  void connectionErrorOccurredWithoutException(@Mock MachineShellClientConnection someManaged) {
    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(someManaged));

    verify(state).onErrorOccurred();
    verify(removeAction).accept(connection);
    verify(someManaged).removeConnectionEventListener(connection);
  }

  @Test
  void connectionErrorOccurredWithException(@Mock MachineShellClientConnection someManaged) {
    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(
        someManaged,
        SOME_CLIENT_EXCEPTION));

    verify(state).onErrorOccurred(SOME_CLIENT_EXCEPTION);
    verify(removeAction).accept(connection);
    verify(someManaged).removeConnectionEventListener(connection);
  }

  @Test
  void getConnection(@Mock MachineShellConnection someHandlerConnection) {
    when(connectionConstructor.construct(connection))
        .thenReturn(someHandlerConnection);

    MachineShellConnection handlerConnection = connection.getConnection();

    assertThat(handlerConnection).isEqualTo(someHandlerConnection);
  }

  @Test
  void requestAvailable() {
    when(state.request(SOME_USERNAME)).thenReturn(AVAILABLE);

    boolean available = connection.request(SOME_USERNAME);

    assertThat(available).isTrue();
  }

  @Test
  void requestUnavailable() {
    when(state.request(SOME_USERNAME)).thenReturn(UNAVAILABLE);

    boolean available = connection.request(SOME_USERNAME);

    assertThat(available).isFalse();
  }

  @Test
  void acquireIt() {
    connection.acquire();

    verify(state).acquire();
  }

  @Test
  void executeCommand(@Mock MachineShellClientCommand<Object> someCommand) throws Exception {
    when(state.execute(someCommand)).thenReturn(SOME_EXECUTION_RESULT);

    Object result = connection.execute(someCommand);

    assertThat(result).isEqualTo(SOME_EXECUTION_RESULT);
  }

  @Test
  void accessFile(@Mock MachineShellClientFile someFile) throws Exception {
    when(state.file(SOME_FILE_PATH)).thenReturn(someFile);

    MachineShellClientFile file = connection.file(SOME_FILE_PATH);

    assertThat(file).isEqualTo(someFile);
  }

  @Test
  void closeImmediately() {
    connection.closeNow();

    verify(state).closeNow();
  }

  @Test
  void releaseIt() {
    connection.release();

    verify(state).release();
  }
}
