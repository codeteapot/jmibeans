package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.MachineShellFile;

abstract class MachineShellConnectionState {

  protected final MachineShellConnectionStateChanger stateChanger;

  protected MachineShellConnectionState(MachineShellConnectionStateChanger stateChanger) {
    this.stateChanger = requireNonNull(stateChanger);
  }

  abstract <R> R execute(MachineShellCommand<R> command)
      throws MachineShellException, MachineShellCommandExecutionException;

  abstract MachineShellFile file(String path) throws MachineShellException;

  abstract void close() throws MachineShellException;
}
