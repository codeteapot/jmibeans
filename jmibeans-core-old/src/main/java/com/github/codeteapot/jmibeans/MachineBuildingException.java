package com.github.codeteapot.jmibeans;

/**
 * Exception that is thrown during the machine build process if an error occurs.
 *
 * @see MachineBuilder#build(MachineBuilderContext)
 */
public class MachineBuildingException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception with an error message.
   *
   * @param message The error message.
   */
  public MachineBuildingException(String message) {
    super(message);
  }

  /**
   * Exception with a cause.
   *
   * @param cause The underlying cause.
   */
  public MachineBuildingException(Throwable cause) {
    super(cause);
  }

  /**
   * Exception with an error message and with a cause.
   *
   * @param message The error message.
   * @param cause The underlying cause.
   */
  public MachineBuildingException(String message, Throwable cause) {
    super(message, cause);
  }
}
