package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

abstract class MachineShellClientConnectionState {

  protected final MachineShellClientConnectionStateChanger stateChanger;
  protected final MachineShellClientConnectionEventSource eventSource;

  protected MachineShellClientConnectionState(
      MachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionEventSource eventSource) {
    this.stateChanger = requireNonNull(stateChanger);
    this.eventSource = requireNonNull(eventSource);
  }

  abstract <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException;

  abstract MachineShellClientFile file(String path) throws MachineShellClientException;

  abstract void close() throws Exception;
}
