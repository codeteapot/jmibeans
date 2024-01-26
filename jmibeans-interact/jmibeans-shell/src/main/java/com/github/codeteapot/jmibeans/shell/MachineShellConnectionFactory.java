package com.github.codeteapot.jmibeans.shell;

public interface MachineShellConnectionFactory {

  MachineShellConnection getConnection(String username) throws MachineShellException;
}
