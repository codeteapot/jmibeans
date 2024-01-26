package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

class PooledMachineShellClientConnectionRequestedState
    extends PooledMachineShellClientConnectionState {

  private final MachineShellClientConnectionBridge bridge;
  private final String username;

  PooledMachineShellClientConnectionRequestedState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username) {
    super(stateChanger);
    this.bridge = requireNonNull(bridge);
    this.username = requireNonNull(username);
  }

  @Override
  boolean request(String username) {
    return false;
  }

  @Override
  void acquire() {
    stateChanger.acquired(bridge, username);
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
    stateChanger.closing();
    bridge.closeNow();
  }

  @Override
  void release() {
    throw new IllegalStateException("Connection is requested");
  }
}
