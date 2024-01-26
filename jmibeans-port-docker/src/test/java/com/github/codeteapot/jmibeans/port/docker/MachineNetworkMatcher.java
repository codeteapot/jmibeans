package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.mockito.ArgumentMatcher;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;

class MachineNetworkMatcher implements ArgumentMatcher<MachineNetwork> {

  private MachineNetworkName name;
  private InetAddress address;

  MachineNetworkMatcher() {
    name = null;
    address = null;
  }

  MachineNetworkMatcher withName(MachineNetworkName name) {
    this.name = requireNonNull(name);
    return this;
  }

  MachineNetworkMatcher withAddress(InetAddress address) {
    this.address = requireNonNull(address);
    return this;
  }

  @Override
  public boolean matches(MachineNetwork argument) {
    try {
      if (name != null && !name.equals(argument.getName())) {
        return false;
      }
      if (address != null && !address.equals(argument.getAddress())) {
        return false;
      }
      return true;
    } catch (UnknownHostException e) {
      return false;
    }
  }
}
