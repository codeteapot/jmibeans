package com.github.codeteapot.jmibeans.port;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.MachineCatalog;
import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 * Type for machine profile name.
 *
 * <p>Required to determine the profile of a machine at the time of consulting the catalog.
 *
 * @see MachineCatalog#getProfile(MachineProfileName)
 * @see MachineLink#getProfileName()
 */
public class MachineProfileName implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Profile name value.
   */
  private final String value;

  /**
   * Profile name given its value.
   *
   * @param value The value of the profile name.
   */
  @ConstructorProperties({
      "value"
  })
  public MachineProfileName(String value) {
    this.value = requireNonNull(value);
  }

  /**
   * Profile name value.
   *
   * @return The value of the profile name.
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
    if (obj instanceof MachineProfileName) {
      MachineProfileName profileName = (MachineProfileName) obj;
      return value.equals(profileName.value);
    }
    return false;
  }

  /**
   * The value of the profile name itself.
   */
  @Override
  public String toString() {
    return value;
  }
}
