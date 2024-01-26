package com.github.codeteapot.jmibeans.shell.client.pool;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.MachineShellFile;

class MachineShellConnectionClosedState extends MachineShellConnectionState {

  MachineShellConnectionClosedState(MachineShellConnectionStateChanger stateChanger) {
    super(stateChanger);
  }

  @Override
  <R> R execute(MachineShellCommand<R> command)
      throws MachineShellException, MachineShellCommandExecutionException {
    throw new MachineShellException("Connection closed");
  }

  @Override
  MachineShellFile file(String path) throws MachineShellException {
    throw new MachineShellException("Connection closed");
  }

  @Override
  void close() throws MachineShellException {
    throw new MachineShellException("Connection already closed");
  }
}
