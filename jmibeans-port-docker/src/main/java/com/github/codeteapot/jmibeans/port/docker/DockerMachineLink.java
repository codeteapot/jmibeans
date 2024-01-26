package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperties;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineProfileName;

class DockerMachineLink implements MachineLink {

  private final MachineProfileName profileName;
  private final MachineBuilderProperties builderProperties;
  private final MachineAgent agent;

  DockerMachineLink(
      MachineProfileName profileName,
      MachineBuilderProperties builderProperties,
      MachineAgent agent) {
    this.profileName = requireNonNull(profileName);
    this.builderProperties = requireNonNull(builderProperties);
    this.agent = requireNonNull(agent);
  }

  @Override
  public MachineProfileName getProfileName() {
    return profileName;
  }

  @Override
  public MachineBuilderProperties getBuilderProperties() {
    return builderProperties;
  }

  @Override
  public MachineAgent getAgent() {
    return agent;
  }
}
