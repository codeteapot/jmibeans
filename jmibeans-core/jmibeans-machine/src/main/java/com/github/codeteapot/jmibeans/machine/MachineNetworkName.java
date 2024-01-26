package com.github.codeteapot.jmibeans.machine;

import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class MachineNetworkName implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String value;

  @ConstructorProperties({
      "value"
  })
  public MachineNetworkName(String value) {
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
    if (obj instanceof MachineNetworkName) {
      MachineNetworkName networkName = (MachineNetworkName) obj;
      return value.equals(networkName.value);
    }
    return false;
  }

  @Override
  public String toString() {
    return value;
  }
}
