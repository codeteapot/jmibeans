package com.github.codeteapot.jmibeans.session;

import java.io.IOException;

class SSHMachineSessionClosed implements SSHMachineSessionState {

  SSHMachineSessionClosed() {}

  @Override
  public <R> R execute(MachineCommand<R> command)
      throws MachineSessionException, MachineCommandExecutionException {
    throw new MachineSessionException("Session was closed");
  }

  @Override
  public MachineSessionFile file(String path) throws MachineSessionException {
    throw new MachineSessionException("Session was closed");
  }

  public void close() throws IOException {
    throw new IOException("Session was closed");
  }
}
