package com.github.codeteapot.jmibeans.port;

import static java.util.Objects.requireNonNull;
import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 * Name that determines the profile of a machine.
 */
public class MachineProfileName implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Textual value of the profile name.
   */
  private final String value;

  /**
   * Creates the profile name given its textual value.
   *
   * @param value The textual value of the profile name.
   */
  @ConstructorProperties({
      "value"
  })
  public MachineProfileName(String value) {
    this.value = requireNonNull(value);
  }

  /**
   * Textual value of the profile name.
   *
   * @return The textual value of the profile name.
   */
  public String getValue() {
    return value;
  }

  /**
   * Hash code based on the textual value of the profile name.
   */
  @Override
  public int hashCode() {
    return value.hashCode();
  }

  /**
   * Equality based on the textual value of the profile name.
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
   * The textual value of the profile name.
   */
  @Override
  public String toString() {
    return value;
  }
}
