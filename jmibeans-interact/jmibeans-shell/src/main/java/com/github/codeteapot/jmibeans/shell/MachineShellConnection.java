package com.github.codeteapot.jmibeans.shell;

import java.nio.file.FileSystem;

public interface MachineShellConnection extends AutoCloseable {

  FileSystem getFileSystem() throws MachineShellException;

  <R> R execute(MachineShellCommand<R> command)
      throws MachineShellException, MachineShellCommandExecutionException;

  @Override
  void close() throws MachineShellException;
}
