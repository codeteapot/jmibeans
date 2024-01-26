package com.github.codeteapot.jmibeans.port.docker;

import static com.github.codeteapot.jmibeans.port.docker.DockerLabels.getRoleLabelValue;
import static java.lang.String.format;
import static java.net.InetAddress.getByName;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.MachineSessionHostResolutionException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.ContainerNetworkSettings;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

class DockerMachineLink implements MachineLink {

  private final DockerProfileResolver profileResolver;
  private final Container container;

  DockerMachineLink(DockerProfileResolver profileResolver, Container container) {
    this.profileResolver = requireNonNull(profileResolver);
    this.container = requireNonNull(container);
  }

  @Override
  public MachineProfileName getProfileName() {
    return getRoleLabelValue(container)
        .flatMap(profileResolver::fromRole)
        .orElseGet(profileResolver::getDefault);
  }

  @Override
  public InetAddress getSessionHost(MachineNetworkName networkName)
      throws MachineSessionHostResolutionException {
    try {
      return getByName(Optional.of(container)
          .map(Container::getNetworkSettings)
          .map(ContainerNetworkSettings::getNetworks)
          .map(networks -> networks.get(networkName.getValue()))
          .map(ContainerNetwork::getIpAddress)
          .orElseThrow(() -> new MachineSessionHostResolutionException(
              format("Could not determine IP of network %s", networkName))));
    } catch (UnknownHostException e) {
      throw new MachineSessionHostResolutionException(e);
    }
  }
}
