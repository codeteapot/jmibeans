package com.github.codeteapot.jmibeans.shell.client;

public class MachineShellClientCommandExecutionException extends Exception {

  private static final long serialVersionUID = 1L;

  public MachineShellClientCommandExecutionException(String message) {
    super(message);
  }

  public MachineShellClientCommandExecutionException(Throwable cause) {
    super(cause);
  }

  public MachineShellClientCommandExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
