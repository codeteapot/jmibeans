package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnectionEvent;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnectionListener;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

class PooledMachineShellClientConnection implements
    MachineShellClientConnectionListener,
    PooledMachineShellClientConnectionStateChanger {

  private final Consumer<PooledMachineShellClientConnection> removeAction;
  private final MachineShellConnectionConstructor connectionConstructor;
  private PooledMachineShellClientConnectionState state;

  PooledMachineShellClientConnection(
      MachineShellClientConnectionBridge bridge,
      String username,
      Consumer<PooledMachineShellClientConnection> removeAction) {
    this(
        removeAction,
        MachineShellConnectionImpl::new,
        stateMapper -> new PooledMachineShellClientConnectionAcquiredState(
            stateMapper,
            bridge,
            username));
  }

  PooledMachineShellClientConnection(
      Consumer<PooledMachineShellClientConnection> removeAction,
      MachineShellConnectionConstructor connectionConstructor,
      PooledMachineShellClientConnectionStateMapper stateMapper) {
    this.removeAction = requireNonNull(removeAction);
    this.connectionConstructor = requireNonNull(connectionConstructor);
    state = stateMapper.map(this);
  }

  @Override
  public void connectionClosed(MachineShellClientConnectionEvent event) {
    connectionLost(event, state::onClosed, state::onClosed);
  }

  @Override
  public void connectionErrorOccurred(MachineShellClientConnectionEvent event) {
    connectionLost(event, state::onErrorOccurred, state::onErrorOccurred);
  }

  // Covered on PoolingMachineShellConnectionFactoryAcceptanceTest
  @Override
  public void changeState(PooledMachineShellClientConnectionState newState) {
    state = requireNonNull(newState);
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
}
