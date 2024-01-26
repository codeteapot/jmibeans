package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import java.nio.file.FileSystem;
import java.util.logging.Logger;

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
  FileSystem getFileSystem() throws MachineShellClientException {
    throw new MachineShellClientException("Connection closing");
  }

  @Override
  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
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
