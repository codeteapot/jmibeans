package com.github.codeteapot.jmibeans.shell;

public interface MachineShellConnectionFactory {

  public static final String DEFAULT_USERNAME = "jmi-user";

  MachineShellConnection getConnection(String username) throws MachineShellException;
}
