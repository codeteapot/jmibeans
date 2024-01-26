package com.github.codeteapot.jmibeans.shell;

public class MachineShellCommandExecutionException extends MachineShellException {

  private static final long serialVersionUID = 1L;

  public MachineShellCommandExecutionException(String message) {
    super(message);
  }

  public MachineShellCommandExecutionException(Throwable cause) {
    super(cause);
  }

  public MachineShellCommandExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
