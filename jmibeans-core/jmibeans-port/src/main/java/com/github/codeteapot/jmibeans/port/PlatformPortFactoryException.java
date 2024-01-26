package com.github.codeteapot.jmibeans.port;

/**
 * Exception occurred during a port factory method call.
 *
 * @see PlatformPortFactory#setProperty(String, Object)
 * @see PlatformPortFactory#getPort()
 */
public class PlatformPortFactoryException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception with no message or cause.
   */
  public PlatformPortFactoryException() {}

  /**
   * Exception with the specified message.
   *
   * @param message The message of the exception.
   */
  public PlatformPortFactoryException(String message) {
    super(message);
  }

  /**
   * Exception with the specified cause.
   *
   * @param cause The cause of the exception.
   */
  public PlatformPortFactoryException(Throwable cause) {
    super(cause);
  }

  /**
   * Exception with the specified message and cause.
   *
   * @param message The message of the exception.
   * @param cause The cause of the exception.
   */
  public PlatformPortFactoryException(String message, Throwable cause) {
    super(message, cause);
  }
}
