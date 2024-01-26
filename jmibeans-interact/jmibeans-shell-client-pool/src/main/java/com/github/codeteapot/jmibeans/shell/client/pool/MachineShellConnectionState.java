package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.nio.file.FileSystem;

abstract class MachineShellConnectionState {

  protected final MachineShellConnectionStateChanger stateChanger;

  protected MachineShellConnectionState(MachineShellConnectionStateChanger stateChanger) {
    this.stateChanger = requireNonNull(stateChanger);
  }

  abstract FileSystem getFileSystem() throws MachineShellException;

  abstract <R> R execute(MachineShellCommand<R> command)
      throws MachineShellException, MachineShellCommandExecutionException;

  abstract void close() throws MachineShellException;
}
