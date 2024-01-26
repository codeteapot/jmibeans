package com.github.codeteapot.jmibeans.shell.client.security.auth;

import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class MachineShellIdentityName implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String value;

  @ConstructorProperties({
      "value"
  })
  public MachineShellIdentityName(String value) {
    this.value = requireNonNull(value);
  }

  public String getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof MachineShellIdentityName) {
      MachineShellIdentityName identityName = (MachineShellIdentityName) obj;
      return value.equals(identityName.value);
    }
    return false;
  }

  @Override
  public String toString() {
    return value;
  }
}
