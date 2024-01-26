package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

class PooledMachineShellClientConnectionClosingState
    extends PooledMachineShellClientConnectionState {

  private static final Logger logger = getLogger(
      PooledMachineShellClientConnection.class.getName());

  PooledMachineShellClientConnectionClosingState(
      PooledMachineShellClientConnectionStateChanger stateChanger) {
    super(stateChanger);
  }

  @Override
  boolean request(String username) {
    return false;
  }

  @Override
  void acquire() {
    throw new IllegalStateException("Connection closing");
  }

  @Override
  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    throw new MachineShellClientException("Connection closing");
  }

  @Override
  MachineShellClientFile file(String path) throws MachineShellClientException {
    throw new MachineShellClientException("Connection closing");
  }

  @Override
  void closeNow() {
    logger.fine("Connection already closing");
  }

  @Override
  void release() {
    throw new IllegalStateException("Connection closing");
  }
}
