package com.github.codeteapot.jmibeans.session;

/**
 * Exception thrown when machine command execution has failed.
 *
 * @see MachineSession#execute(MachineCommand)
 */
public class MachineCommandExecutionException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception with an error message.
   *
   * @param message The error message.
   */
  public MachineCommandExecutionException(String message) {
    super(message);
  }

  /**
   * Exception with a cause.
   *
   * @param cause The underlying cause.
   */
  public MachineCommandExecutionException(Throwable cause) {
    super(cause);
  }

  /**
   * Exception with an error message and with a cause.
   *
   * @param message The error message.
   * @param cause The underlying cause.
   */
  public MachineCommandExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
