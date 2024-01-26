package com.github.codeteapot.jmibeans.port.docker;

import static com.github.codeteapot.jmibeans.port.docker.DockerLabels.getProfileLabelValue;

import com.github.codeteapot.jmibeans.port.MachineBuilderProperties;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.dockerjava.api.model.Container;
import com.google.common.base.Supplier;
import java.util.Optional;

class DockerMonitorStartContext {

  private final Supplier<Optional<MachineProfileName>> profileNameSupplier;
  private final MachineBuilderProperties builderProperties;
  private final DockerMachineAgent agent;

  DockerMonitorStartContext(Container container) {
    profileNameSupplier = () -> getProfileLabelValue(container).map(MachineProfileName::new);
    builderProperties = new DockerMachineBuilderProperties(container);
    agent = new DockerMachineAgent(container);
  }

  Optional<MachineProfileName> getProfileName() {
    return profileNameSupplier.get();
  }

  MachineBuilderProperties getBuilderProperties() {
    return builderProperties;
  }

  DockerMachineAgent getAgent() {
    return agent;
  }
}
