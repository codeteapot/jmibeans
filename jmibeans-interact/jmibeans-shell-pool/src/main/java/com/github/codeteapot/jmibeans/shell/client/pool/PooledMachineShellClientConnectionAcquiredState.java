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
  private final PooledMachineShellClientConnectionAvailableStateConstructor availStateConstructor;
  private final PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor;

  PooledMachineShellClientConnectionAcquiredState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username) {
    this(
        stateChanger,
        bridge,
        username,
        PooledMachineShellClientConnectionAvailableState::new,
        PooledMachineShellClientConnectionClosingState::new);
  }

  PooledMachineShellClientConnectionAcquiredState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username,
      PooledMachineShellClientConnectionAvailableStateConstructor availStateConstructor,
      PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor) {
    super(stateChanger);
    this.bridge = requireNonNull(bridge);
    this.username = requireNonNull(username);
    this.availStateConstructor = requireNonNull(availStateConstructor);
    this.closingStateConstructor = requireNonNull(closingStateConstructor);
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
    stateChanger.changeState(closingStateConstructor.construct(stateChanger));
    bridge.closeNow();
  }

  @Override
  void release() {
    stateChanger.changeState(availStateConstructor.construct(
        stateChanger,
        bridge,
        username,
        bridge.closeIdleTimeout(this::changeStateClosing)));
  }

  private Void changeStateClosing() {
    stateChanger.changeState(closingStateConstructor.construct(stateChanger));
    return null;
  }
}
