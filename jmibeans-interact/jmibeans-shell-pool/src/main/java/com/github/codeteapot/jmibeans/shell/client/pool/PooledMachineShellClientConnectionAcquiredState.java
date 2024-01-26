package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

class PooledMachineShellClientConnectionAcquiredState
    extends PooledMachineShellClientConnectionState {

  private final MachineShellClientConnectionBridge bridge;
  private final String username;

  PooledMachineShellClientConnectionAcquiredState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username) {
    super(stateChanger);
    this.bridge = requireNonNull(bridge);
    this.username = requireNonNull(username);
  }

  @Override
  boolean request(String username) {
    throw new IllegalStateException("Connection acquired");
  }

  @Override
  void acquire() {
    throw new IllegalStateException("Connection already acquired");
  }

  @Override
  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    return bridge.execute(command);
  }

  @Override
  MachineShellClientFile file(String path) throws MachineShellClientException {
    return bridge.file(path);
  }

  @Override
  void closeNow() {
    stateChanger.closing();
    bridge.closeNow();
  }

  @Override
  void release() {
    stateChanger.available(bridge, username, bridge.closeIdleTimeout(this::changeStateClosing));
  }

  private Void changeStateClosing() {
    stateChanger.closing();
    return null;
  }
}
