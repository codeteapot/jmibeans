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
  private final PooledMachineShellClientConnectionAcquiredStateConstructor acquiredStateConstructor;
  private final PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor;

  PooledMachineShellClientConnectionRequestedState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username) {
    this(
        stateChanger,
        bridge,
        username,
        PooledMachineShellClientConnectionAcquiredState::new,
        PooledMachineShellClientConnectionClosingState::new);
  }

  PooledMachineShellClientConnectionRequestedState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username,
      PooledMachineShellClientConnectionAcquiredStateConstructor acquiredStateConstructor,
      PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor) {
    super(stateChanger);
    this.bridge = requireNonNull(bridge);
    this.username = requireNonNull(username);
    this.acquiredStateConstructor = requireNonNull(acquiredStateConstructor);
    this.closingStateConstructor = requireNonNull(closingStateConstructor);
  }

  @Override
  boolean request(String username) {
    return false;
  }

  @Override
  void acquire() {
    stateChanger.changeState(acquiredStateConstructor.construct(
        stateChanger,
        bridge,
        username));
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
    throw new IllegalStateException("Connection is requested");
  }
}
