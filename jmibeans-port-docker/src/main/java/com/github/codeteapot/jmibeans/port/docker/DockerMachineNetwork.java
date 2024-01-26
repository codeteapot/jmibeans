package com.github.codeteapot.jmibeans.port.docker;

import static java.net.InetAddress.getByName;
import static java.util.Objects.requireNonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.AbstractMachineNetwork;
import com.github.dockerjava.api.model.ContainerNetwork;

class DockerMachineNetwork extends AbstractMachineNetwork {

  private final ContainerNetwork containerNetwork;

  DockerMachineNetwork(String name, ContainerNetwork containerNetwork) {
    super(new MachineNetworkName(name));
    this.containerNetwork = requireNonNull(containerNetwork);
  }

  @Override
  public InetAddress getAddress() throws UnknownHostException {
    return getByName(containerNetwork.getIpAddress());
  }
}
