package com.github.codeteapot.jmibeans.session;

import java.io.IOException;

interface SSHMachineSessionState {

  <R> R execute(MachineCommand<R> command)
      throws MachineSessionException, MachineCommandExecutionException;

  MachineSessionFile file(String path) throws MachineSessionException;

  void close() throws IOException;
}
