package com.github.codeteapot.jmibeans.shell.client;

public interface MachineShellClientConnectionFactory {

  MachineShellClientConnection getConnection(String username) throws MachineShellClientException;
}
