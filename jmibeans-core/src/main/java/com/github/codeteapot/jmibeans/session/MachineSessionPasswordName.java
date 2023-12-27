package com.github.codeteapot.jmibeans.session;

import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 * Name of a password.
 *
 * <p>Provides the possibility of indicating the passwords that will be used during the password
 * authentication process.
 *
 * @see MachineSessionAuthenticationContext#addPassword(MachineSessionPasswordName)
 */
public class MachineSessionPasswordName implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Password name value.
   */
  private final String value;

  /**
   * Password name given its value.
   *
   * @param value The value of the password name.
   */
  @ConstructorProperties({
      "value"
  })
  public MachineSessionPasswordName(String value) {
    this.value = requireNonNull(value);
  }

  /**
   * Password name value.
   *
   * @return The value of the password name.
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
    if (obj instanceof MachineSessionPasswordName) {
      MachineSessionPasswordName passwordName = (MachineSessionPasswordName) obj;
      return value.equals(passwordName.value);
    }
    return false;
  }

  /**
   * The value of the password name itself.
   */
  @Override
  public String toString() {
    return value;
  }
}
