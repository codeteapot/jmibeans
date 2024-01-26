package com.github.codeteapot.jmibeans.session;

/**
 * Exception thrown when some machine session operation fails.
 *
 * <p>It may be due to a connection error, an authentication failure or any other issue related to
 * the machine on which the session is intended to be established.
 *
 * @see MachineSession#execute(MachineCommand)
 * @see MachineSession#file(String)
 */
public class MachineSessionException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception with an error message.
   *
   * @param message The error message.
   */
  public MachineSessionException(String message) {
    super(message);
  }

  /**
   * Exception with a cause.
   *
   * @param cause The underlying cause.
   */
  public MachineSessionException(Throwable cause) {
    super(cause);
  }

  /**
   * Exception with an error message and with a cause.
   *
   * @param message The error message.
   * @param cause The underlying cause.
   */
  public MachineSessionException(String message, Throwable cause) {
    super(message, cause);
  }
}
