package com.github.codeteapot.jmibeans.session;

import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 * Name of an identity.
 *
 * <p>It gives the possibility of identifying the identity that can exclusively perform public key
 * authentication.
 *
 * @see MachineSessionAuthenticationContext#setIdentityOnly(MachineSessionIdentityName)
 */
public class MachineSessionIdentityName implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Identity name value.
   */
  private final String value;

  /**
   * Identity name given its value.
   *
   * @param value The value of the identity name.
   */
  @ConstructorProperties({
      "value"
  })
  public MachineSessionIdentityName(String value) {
    this.value = requireNonNull(value);
  }

  /**
   * Identity name value.
   *
   * @return The value of the identity name.
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
    if (obj instanceof MachineSessionIdentityName) {
      MachineSessionIdentityName identityName = (MachineSessionIdentityName) obj;
      return value.equals(identityName.value);
    }
    return false;
  }

  /**
   * The value of the identity name itself.
   */
  @Override
  public String toString() {
    return value;
  }
}
