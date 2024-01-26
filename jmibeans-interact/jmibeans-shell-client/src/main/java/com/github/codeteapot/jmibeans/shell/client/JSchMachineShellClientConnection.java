package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import com.jcraft.jsch.Session;
import java.nio.file.FileSystem;
import java.util.function.Consumer;

class JSchMachineShellClientConnection implements MachineShellClientConnection {

  private final MachineShellClientConnectionEventDispatcher eventDispatcher;
  private MachineShellClientConnectionState state;

  JSchMachineShellClientConnection(Session jschSession, long executionTimeoutMillis) {
    eventDispatcher = new MachineShellClientConnectionEventDispatcher(this);
    state = initial(
        newState -> state = requireNonNull(newState),
        eventDispatcher,
        jschSession,
        executionTimeoutMillis);
  }

  @Override
  public FileSystem getFileSystem() throws MachineShellClientException {
    return state.getFileSystem();
  }

  @Override
  public <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    return state.execute(command);
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
    return new JSchMachineShellClientConnectionAvailableState(
        new MachineShellClientConnectionStateChanger(changeStateAction, eventSource),
        eventSource,
        jschSession,
        executionTimeoutMillis);
  }
}
