package com.github.codeteapot.jmibeans.port;

import com.github.codeteapot.jmibeans.MachineBuilderContext;
import com.github.codeteapot.jmibeans.machine.MachineContext;

/**
 * Exception thrown when a network is not recognized or the address of a machine cannot be resolved.
 * 
 * <p>It is used in the {@link MachineLink#getSessionHost(MachineNetworkName)} method implementation
 * in case the address of the machine cannot be determined.
 *
 * @see MachineBuilderContext#getSession(String)
 * @see MachineContext#getSession(String)
 */
public class MachineSessionHostResolutionException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception with an error message.
   *
   * @param message The error message.
   */
  public MachineSessionHostResolutionException(String message) {
    super(message);
  }

  /**
   * Exception with a cause.
   *
   * @param cause The underlying cause.
   */
  public MachineSessionHostResolutionException(Throwable cause) {
    super(cause);
  }

  /**
   * Exception with an error message and with a cause.
   *
   * @param message The error message.
   * @param cause The underlying cause.
   */
  public MachineSessionHostResolutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
