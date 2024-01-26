package com.github.codeteapot.jmibeans.port.docker;

import static com.github.codeteapot.jmibeans.port.docker.DockerLabels.getBuilderProps;
import static com.github.codeteapot.jmibeans.port.docker.DockerLabels.getRoleLabelValue;

import com.github.codeteapot.jmibeans.port.MachineBuilderPropertyResolver;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.dockerjava.api.model.Container;

class DockerMonitorStartContext {

  private final MachineProfileName profileName;
  private final MachineBuilderPropertyResolver builderPropertyResolver;
  private final DockerMachineAgent agent;

  DockerMonitorStartContext(DockerProfileResolver profileResolver, Container container) {
    profileName = getRoleLabelValue(container)
        .flatMap(profileResolver::fromRole)
        .orElseGet(profileResolver::getDefault);
    builderPropertyResolver = new MapMachineBuilderPropertyResolver(getBuilderProps(container));
    agent = new DockerMachineAgent(container);
  }

  MachineProfileName getProfileName() {
    return profileName;
  }

  MachineBuilderPropertyResolver getBuilderPropertyResolver() {
    return builderPropertyResolver;
  }

  DockerMachineAgent getAgent() {
    return agent;
  }
}
