package com.github.codeteapot.jmibeans.port.docker;

import static java.net.InetAddress.getByName;
import static java.util.Objects.requireNonNull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.dockerjava.api.model.ContainerNetwork;

class DockerMachineNetwork implements MachineNetwork {

  private final MachineNetworkName name;
  private final ContainerNetwork containerNetwork;

  DockerMachineNetwork(String name, ContainerNetwork containerNetwork) {
    this.name = new MachineNetworkName(name);
    this.containerNetwork = requireNonNull(containerNetwork);
  }

  @Override
  public MachineNetworkName getName() {
    return name;
  }

  @Override
  public InetAddress getAddress() throws UnknownHostException {
    return getByName(containerNetwork.getIpAddress());
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
    if (obj instanceof DockerMachineNetwork) {
      DockerMachineNetwork network = (DockerMachineNetwork) obj;
      return name.equals(network.name);
    }
    return false;
  }
}
