package com.github.codeteapot.jmibeans.shell.client;

import java.nio.file.FileSystem;

public interface MachineShellClientConnection extends AutoCloseable {

  FileSystem getFileSystem() throws MachineShellClientException;

  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException;

  void addConnectionEventListener(MachineShellClientConnectionListener listener);

  void removeConnectionEventListener(MachineShellClientConnectionListener listener);
}
