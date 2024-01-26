package com.github.codeteapot.jmibeans.port;

import static java.util.Objects.requireNonNull;
import java.io.Serializable;

/**
 * Property defined in infrastructure for the build of a machine.
 *
 * <p>
 * The property value is raw. That is, it is available as a {@link String} representation.
 * 
 * @see MachineBuilderProperties
 */
public class MachineBuilderProperty implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Name of a machine builder property.
   */
  private final String name;

  /**
   * Raw value of a machine builder property.
   */
  private final String rawValue;

  /**
   * Builder with the name and raw value of the property.
   *
   * @param name The name of the property.
   * @param rawValue The raw value of the property.
   */
  public MachineBuilderProperty(String name, String rawValue) {
    this.name = requireNonNull(name);
    this.rawValue = requireNonNull(rawValue);
  }

  /**
   * Name of a machine builder property.
   *
   * @return The name of the property.
   */
  public String getName() {
    return name;
  }

  /**
   * Raw value of a machine builder property.
   *
   * @return The raw value of the property.
   */
  public String getRawValue() {
    return rawValue;
  }
}
