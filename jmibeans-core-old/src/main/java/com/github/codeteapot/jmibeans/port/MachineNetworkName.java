package com.github.codeteapot.jmibeans.port;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.MachineProfile;
import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 * Type for network name.
 *
 * <p>Required to determine the address that is used to establish sessions on a machine based on the
 * characteristics of the platform port.
 *
 * @see MachineProfile#getNetworkName()
 * @see MachineLink#getSessionHost(MachineNetworkName)
 */
public class MachineNetworkName implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Network name value.
   */
  private final String value;

  /**
   * Network name given its value.
   *
   * @param value The value of the network name.
   */
  @ConstructorProperties({
      "value"
  })
  public MachineNetworkName(String value) {
    this.value = requireNonNull(value);
  }

  /**
   * Network name value.
   *
   * @return The value of the network name.
   */
  public String getValue() {
    return value;
  }

  /**
   * Based on value.
   */
  @Override
  public int hashCode() {
    return value.hashCode();
  }

  /**
   * Based on value.
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
   * The value of the network name itself.
   */
  @Override
  public String toString() {
    return value;
  }
}
