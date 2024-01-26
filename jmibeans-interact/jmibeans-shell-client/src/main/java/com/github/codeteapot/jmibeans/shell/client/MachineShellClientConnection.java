package com.github.codeteapot.jmibeans.shell.client;

public interface MachineShellClientConnection extends AutoCloseable {

  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException;

  MachineShellClientFile file(String path) throws MachineShellClientException;

  void addConnectionEventListener(MachineShellClientConnectionListener listener);

  void removeConnectionEventListener(MachineShellClientConnectionListener listener);
}
