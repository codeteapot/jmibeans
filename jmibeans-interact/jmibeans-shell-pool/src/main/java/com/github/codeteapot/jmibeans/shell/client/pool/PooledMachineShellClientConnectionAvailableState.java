package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

class PooledMachineShellClientConnectionAvailableState
    extends PooledMachineShellClientConnectionState {

  private final MachineShellClientConnectionBridge bridge;
  private final String username;
  private final MachineShellClientConnectionBridgeCloseTask closeTask;
  private final PooledMachineShellClientConnectionRequestedStateConstructor reqStateConstructor;
  private final PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor;

  PooledMachineShellClientConnectionAvailableState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username,
      MachineShellClientConnectionBridgeCloseTask closeTask) {
    this(
        stateChanger,
        bridge,
        username,
        closeTask,
        PooledMachineShellClientConnectionRequestedState::new,
        PooledMachineShellClientConnectionClosingState::new);
  }

  PooledMachineShellClientConnectionAvailableState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username,
      MachineShellClientConnectionBridgeCloseTask closeTask,
      PooledMachineShellClientConnectionRequestedStateConstructor reqStateConstructor,
      PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor) {
    super(stateChanger);
    this.bridge = requireNonNull(bridge);
    this.username = requireNonNull(username);
    this.closeTask = requireNonNull(closeTask);
    this.closingStateConstructor = requireNonNull(closingStateConstructor);
    this.reqStateConstructor = requireNonNull(reqStateConstructor);
  }

  @Override
  boolean request(String username) {
    if (username.equals(this.username)) {
      closeTask.cancel();
      stateChanger.changeState(reqStateConstructor.construct(
          stateChanger,
          bridge,
          username));
      return true;
    }
    return false;
  }

  @Override
  void acquire() {
    throw new IllegalStateException("Connection is not requested");
  }

  @Override
  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    throw new MachineShellClientException("Connection is not acquired");
  }

  @Override
  MachineShellClientFile file(String path) throws MachineShellClientException {
    throw new MachineShellClientException("Connection is not acquired");
  }

  @Override
  void closeNow() {
    stateChanger.changeState(closingStateConstructor.construct(stateChanger));
    bridge.closeNow();
  }

  @Override
  void release() {
    throw new IllegalStateException("Connection is already released");
  }
}