package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import java.nio.file.FileSystem;

class MachineShellConnectionActiveState extends MachineShellConnectionState {

  private final PooledMachineShellClientConnection pooled;

  MachineShellConnectionActiveState(
      MachineShellConnectionStateChanger stateChanger,
      PooledMachineShellClientConnection pooled) {
    super(stateChanger);
    this.pooled = requireNonNull(pooled);
  }

  @Override
  FileSystem getFileSystem() throws MachineShellException {
    try {
      return pooled.getFileSystem();
    } catch (MachineShellClientException e) {
      throw new MachineShellException(e);
    }
  }

  @Override
  <R> R execute(MachineShellCommand<R> command)
      throws MachineShellException, MachineShellCommandExecutionException {
    try {
      return pooled.execute(new MachineShellCommandWrapper<>(command));
    } catch (MachineShellClientCommandExecutionException e) {
      throw new MachineShellCommandExecutionException(e);
    } catch (MachineShellClientException e) {
      throw new MachineShellException(e);
    }
  }

  @Override
  void close() throws MachineShellException {
    stateChanger.closed();
    pooled.release();
  }
}
