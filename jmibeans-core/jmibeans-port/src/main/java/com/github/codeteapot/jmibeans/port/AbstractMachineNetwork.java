package com.github.codeteapot.jmibeans.port;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;

public abstract class AbstractMachineNetwork implements MachineNetwork {

  private final MachineNetworkName name;

  protected AbstractMachineNetwork(MachineNetworkName name) {
    this.name = requireNonNull(name);
  }

  @Override
  public MachineNetworkName getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof AbstractMachineNetwork) {
      AbstractMachineNetwork network = (AbstractMachineNetwork) obj;
      return name.equals(network.name);
    }
    return false;
  }
}
