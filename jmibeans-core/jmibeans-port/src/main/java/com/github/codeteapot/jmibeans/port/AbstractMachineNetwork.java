package com.github.codeteapot.jmibeans.port;

import static java.util.Objects.requireNonNull;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;

/**
 * Implementation of the expected behaviors of any machine network.
 *
 * <p>
 * The equality of the networks is based on the name used to publicly identify them.
 *
 * <p>
 * It must be fully implemented by the infrastructure provider.
 */
public abstract class AbstractMachineNetwork implements MachineNetwork {

  private final MachineNetworkName name;

  /**
   * Provides a public name to the machine network.
   *
   * @param name The public name of the network.
   */
  protected AbstractMachineNetwork(MachineNetworkName name) {
    this.name = requireNonNull(name);
  }

  @Override
  public MachineNetworkName getName() {
    return name;
  }

  /**
   * Hash code based on the network public name.
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /**
   * Two machine networks are the same if, and only if, they have the same public name.
   */
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
