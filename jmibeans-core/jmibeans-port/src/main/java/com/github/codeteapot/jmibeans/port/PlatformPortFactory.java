package com.github.codeteapot.jmibeans.port;

/**
 * Factory for a specific type of platform port.
 *
 * <p>
 * Ready to be configured with flat properties using the {@link #setProperty(String, Object)}
 * method.
 */
public interface PlatformPortFactory {

  /**
   * Type of platform port that creates this factory.
   *
   * @return The string that represents the platform port type.
   */
  public String getType();

  /**
   * Create a new platform port using the provided configuration.
   *
   * @return The platform port that has been created.
   *
   * @throws PlatformPortFactoryException In case of error when creating the platform port.
   */
  public PlatformPort getPort() throws PlatformPortFactoryException;

  /**
   * Set the value of a flat property to configure the factory.
   *
   * @param name The name of the property.
   * @param value The value of the property.
   *
   * @throws PlatformPortFactoryException If the change of property is conflictive.
   */
  public void setProperty(String name, Object value) throws PlatformPortFactoryException;
}
