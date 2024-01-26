package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

import org.mockito.ArgumentMatcher;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;

class MachineAgentMatcher implements ArgumentMatcher<MachineAgent> {

  private CollectionMatcher<MachineNetwork> networks;

  MachineAgentMatcher() {
    networks = null;
  }

  MachineAgentMatcher withNetworks(CollectionMatcher<MachineNetwork> networks) {
    this.networks = requireNonNull(networks);
    return this;
  }

  @Override
  public boolean matches(MachineAgent argument) {
    if (networks != null && !networks.matches(argument.getNetworks())) {
      return false;
    }
    return true;
  }
}
