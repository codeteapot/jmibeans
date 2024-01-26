package com.github.codeteapot.jmibeans.shell;

public interface MachineShellConnection extends AutoCloseable {

  <R> R execute(MachineShellCommand<R> command)
      throws MachineShellException, MachineShellCommandExecutionException;

  MachineShellFile file(String path) throws MachineShellException;

  @Override
  void close() throws MachineShellException;
}
