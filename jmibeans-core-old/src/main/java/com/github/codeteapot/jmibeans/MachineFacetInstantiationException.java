package com.github.codeteapot.jmibeans;

/**
 * Exception thrown when an error occurs during instantiation of a machine facet.
 *
 * <p>The process of building a machine is arranged so that this exception causes a
 * {@link MachineBuildingException}.
 *
 * @see MachineFacetFactory#getFacet(com.github.codeteapot.jmibeans.machine.MachineContext)
 * @see MachineBuilderContext#register(MachineFacetFactory)
 */
public class MachineFacetInstantiationException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception with an error message.
   *
   * @param message The error message.
   */
  public MachineFacetInstantiationException(String message) {
    super(message);
  }

  /**
   * Exception with a cause.
   *
   * @param cause The underlying cause.
   */
  public MachineFacetInstantiationException(Throwable cause) {
    super(cause);
  }

  /**
   * Exception with an error message and with a cause.
   *
   * @param message The error message.
   * @param cause The underlying cause.
   */
  public MachineFacetInstantiationException(String message, Throwable cause) {
    super(message, cause);
  }
}
