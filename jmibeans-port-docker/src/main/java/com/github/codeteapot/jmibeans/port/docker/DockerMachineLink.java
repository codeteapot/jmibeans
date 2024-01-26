package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.port.MachineBuilderPropertyResolver;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineProfileName;

class DockerMachineLink implements MachineLink {

  private final MachineProfileName profileName;
  private final MachineBuilderPropertyResolver builderPropertyResolver;
  private final MachineAgent agent;

  DockerMachineLink(
      MachineProfileName profileName,
      MachineBuilderPropertyResolver builderPropertyResolver,
      MachineAgent agent) {
    this.profileName = requireNonNull(profileName);
    this.builderPropertyResolver = requireNonNull(builderPropertyResolver);
    this.agent = requireNonNull(agent);
  }

  @Override
  public MachineProfileName getProfileName() {
    return profileName;
  }

  @Override
  public MachineBuilderPropertyResolver getBuilderPropertyResolver() {
    return builderPropertyResolver;
  }

  @Override
  public MachineAgent getAgent() {
    return agent;
  }
}
