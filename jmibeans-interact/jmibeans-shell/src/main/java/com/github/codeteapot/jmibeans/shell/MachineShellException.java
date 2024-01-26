package com.github.codeteapot.jmibeans.shell;

public class MachineShellException extends Exception {

  private static final long serialVersionUID = 1L;

  public MachineShellException(String message) {
    super(message);
  }

  public MachineShellException(Throwable cause) {
    super(cause);
  }

  public MachineShellException(String message, Throwable cause) {
    super(message, cause);
  }
}
