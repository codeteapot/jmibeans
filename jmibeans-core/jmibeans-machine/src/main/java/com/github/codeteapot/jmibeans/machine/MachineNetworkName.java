package com.github.codeteapot.jmibeans.machine;

import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 * Name of the network of an infrastructure provider.
 *
 * @see MachineNetwork
 */
public class MachineNetworkName implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Textual value of the network name.
   */
  private final String value;

  /**
   * Creates a network name given its textual value.
   *
   * @param value The textual value of the network name.
   */
  @ConstructorProperties({
      "value"
  })
  public MachineNetworkName(String value) {
    this.value = requireNonNull(value);
  }

  /**
   * Textual value of the network name.
   *
   * @return The textual value of the network name.
   */
  public String getValue() {
    return value;
  }

  /**
   * Hash code based on the textual value of the network name.
   */
  @Override
  public int hashCode() {
    return value.hashCode();
  }

  /**
   * Equality based on the textual value of the network name.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof MachineNetworkName) {
      MachineNetworkName networkName = (MachineNetworkName) obj;
      return value.equals(networkName.value);
    }
    return false;
  }

  /**
   * The textual value of the network name.
   */
  @Override
  public String toString() {
    return value;
  }
}
