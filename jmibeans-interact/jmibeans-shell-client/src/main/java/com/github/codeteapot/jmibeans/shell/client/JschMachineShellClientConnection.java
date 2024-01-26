package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import com.jcraft.jsch.Session;
import java.util.function.Consumer;

class JschMachineShellClientConnection implements MachineShellClientConnection {

  private final MachineShellClientConnectionEventDispatcher eventDispatcher;
  private MachineShellClientConnectionState state;

  JschMachineShellClientConnection(Session jschSession, long executionTimeoutMillis) {
    eventDispatcher = new MachineShellClientConnectionEventDispatcher(this);
    state = initial(
        newState -> state = requireNonNull(newState),
        eventDispatcher,
        jschSession,
        executionTimeoutMillis);
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
  public void addConnectionEventListener(MachineShellClientConnectionListener listener) {
    eventDispatcher.addConnectionEventListener(listener);
  }

  @Override
  public void removeConnectionEventListener(MachineShellClientConnectionListener listener) {
    eventDispatcher.removeConnectionEventListener(listener);
  }

  private static MachineShellClientConnectionState initial(
      Consumer<MachineShellClientConnectionState> changeStateAction,
      MachineShellClientConnectionEventSource eventSource,
      Session jschSession,
      long executionTimeoutMillis) {
    return new JschMachineShellClientConnectionAvailableState(
        new MachineShellClientConnectionStateChanger(changeStateAction, eventSource),
        eventSource,
        jschSession,
        executionTimeoutMillis);
  }
}
