package com.github.codeteapot.jmibeans.shell.client;

public class MachineShellClientException extends Exception {

  private static final long serialVersionUID = 1L;

  public MachineShellClientException(String message) {
    super(message);
  }

  public MachineShellClientException(Throwable cause) {
    super(cause);
  }

  public MachineShellClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
