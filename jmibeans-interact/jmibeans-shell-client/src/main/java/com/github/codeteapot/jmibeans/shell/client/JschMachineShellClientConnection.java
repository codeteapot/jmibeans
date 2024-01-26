package com.github.codeteapot.jmibeans.shell.client;

import com.jcraft.jsch.Session;

class JschMachineShellClientConnection
    implements MachineShellClientConnection, MachineShellClientConnectionStateChanger {

  private final MachineShellClientConnectionEventDispatcher eventDispatcher;
  private MachineShellClientConnectionState state;

  JschMachineShellClientConnection(Session jschSession, long executionTimeoutMillis) {
    this((stateChanger, eventDispatcher) -> new JschMachineShellClientConnectionAvailableState(
        stateChanger,
        eventDispatcher,
        jschSession,
        executionTimeoutMillis));
  }

  JschMachineShellClientConnection(MachineShellClientConnectionStateMapper stateMapper) {
    eventDispatcher = new MachineShellClientConnectionEventDispatcher(this);
    state = stateMapper.map(this, eventDispatcher);
  }

  @Override
  public <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    return state.execute(command);
  }

  @Override
  public MachineShellClientFile file(String path) throws MachineShellClientException {
    return state.file(path);
  }

  @Override
  public void close() throws Exception {
    state.close();
  }

  @Override
  public void changeState(MachineShellClientConnectionState newState) {
    state = newState;
  }

  @Override
  public void addConnectionEventListener(MachineShellClientConnectionListener listener) {
    eventDispatcher.addConnectionEventListener(listener);
  }

  @Override
  public void removeConnectionEventListener(MachineShellClientConnectionListener listener) {
    eventDispatcher.removeConnectionEventListener(listener);
  }
}
