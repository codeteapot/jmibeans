package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnectionEvent;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnectionListener;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;
import java.util.function.Consumer;
import java.util.function.Function;

class PooledMachineShellClientConnection implements MachineShellClientConnectionListener {

  private final Consumer<PooledMachineShellClientConnection> removeAction;
  private final MachineShellConnectionConstructor connectionConstructor;
  private PooledMachineShellClientConnectionState state;

  PooledMachineShellClientConnection(
      MachineShellClientConnectionBridge bridge,
      String username,
      Consumer<PooledMachineShellClientConnection> removeAction) {
    this(removeAction, MachineShellConnectionImpl::new, changeStateAction -> initial(
        changeStateAction,
        bridge,
        username));
  }

  PooledMachineShellClientConnection(
      Consumer<PooledMachineShellClientConnection> removeAction,
      MachineShellConnectionConstructor connectionConstructor,
      Function< //
          Consumer<PooledMachineShellClientConnectionState>, //
          PooledMachineShellClientConnectionState> stateMapper) {
    this.removeAction = requireNonNull(removeAction);
    this.connectionConstructor = requireNonNull(connectionConstructor);
    state = stateMapper.apply(newState -> state = requireNonNull(newState));
  }

  @Override
  public void connectionClosed(MachineShellClientConnectionEvent event) {
    connectionLost(event, state::onClosed, state::onClosed);
  }

  @Override
  public void connectionErrorOccurred(MachineShellClientConnectionEvent event) {
    connectionLost(event, state::onErrorOccurred, state::onErrorOccurred);
  }

  MachineShellConnection getConnection() {
    return connectionConstructor.construct(this);
  }

  synchronized boolean request(String username) {
    return state.request(username);
  }

  void acquire() {
    state.acquire();
  }

  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    return state.execute(command);
  }

  MachineShellClientFile file(String path) throws MachineShellClientException {
    return state.file(path);
  }

  void closeNow() {
    state.closeNow();
  }

  void release() {
    state.release();
  }

  private void connectionLost(
      MachineShellClientConnectionEvent event,
      Consumer<MachineShellClientException> withException,
      Runnable withoutException) {
    event.getClientException()
        .map(exception -> accept(withException, exception))
        .orElseGet(() -> withoutException)
        .run();
    removeAction.accept(this);
    MachineShellClientConnection managed = (MachineShellClientConnection) event.getSource();
    managed.removeConnectionEventListener(this);
  }

  private static Runnable accept(
      Consumer<MachineShellClientException> consumer,
      MachineShellClientException exception) {
    return () -> consumer.accept(exception);
  }

  private static PooledMachineShellClientConnectionState initial(
      Consumer<PooledMachineShellClientConnectionState> changeStateAction,
      MachineShellClientConnectionBridge bridge,
      String username) {
    return new PooledMachineShellClientConnectionAcquiredState(
        new PooledMachineShellClientConnectionStateChanger(
            changeStateAction,
            PooledMachineShellClientConnectionAcquiredState::new,
            PooledMachineShellClientConnectionAvailableState::new,
            PooledMachineShellClientConnectionClosedState::new,
            PooledMachineShellClientConnectionClosingState::new,
            PooledMachineShellClientConnectionErrorOccurredState::new,
            PooledMachineShellClientConnectionRequestedState::new),
        bridge,
        username);
  }
}
