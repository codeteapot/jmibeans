package com.github.codeteapot.jmibeans.shell.client;

import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;

class MachineShellClientConnectionUnavailableState extends MachineShellClientConnectionState {

  private static final Logger logger = getLogger(MachineShellClientConnection.class.getName());

  MachineShellClientConnectionUnavailableState(
      MachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionEventSource eventSource) {
    super(stateChanger, eventSource);
  }

  @Override
  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    throw new MachineShellClientException("Connection unavailable");
  }

  @Override
  MachineShellClientFile file(String path) throws MachineShellClientException {
    throw new MachineShellClientException("Connection unavailable");
  }

  @Override
  void close() throws Exception {
    logger.warning("Connection unavailable");
  }
}
