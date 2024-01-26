package com.github.codeteapot.jmibeans.session;

import static java.util.Objects.requireNonNull;

import com.jcraft.jsch.JSch;
import java.io.IOException;
import java.net.InetAddress;

class SSHMachineSession implements MachineSession, SSHMachineSessionStateChanger {

  private SSHMachineSessionState state;

  SSHMachineSession(
      SSHMachineSessionPasswordMapper passwordMapper,
      long executionTimeoutMillis,
      JSch jsch,
      InetAddress host,
      Integer port,
      String username,
      MachineSessionAuthentication authentication) {
    state = new SSHMachineSessionReady(
        this,
        passwordMapper,
        executionTimeoutMillis,
        jsch,
        host,
        port,
        username,
        authentication);
  }

  @Override
  public <R> R execute(MachineCommand<R> command)
      throws MachineSessionException, MachineCommandExecutionException {
    return state.execute(command);
  }

  @Override
  public MachineSessionFile file(String path) throws MachineSessionException {
    return state.file(path);
  }

  public void close() throws IOException {
    state.close();
  }

  @Override
  public void stateChange(SSHMachineSessionState newState) {
    state = requireNonNull(newState);
  }
}
