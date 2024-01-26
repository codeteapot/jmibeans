package com.github.codeteapot.jmibeans.port.docker;

import static com.github.codeteapot.jmibeans.port.docker.DockerLabels.getBuilderProperties;
import static java.util.Objects.requireNonNull;
import java.util.Iterator;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperties;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperty;
import com.github.dockerjava.api.model.Container;

class DockerMachineBuilderProperties implements MachineBuilderProperties {

  private final Container container;

  DockerMachineBuilderProperties(Container container) {
    this.container = requireNonNull(container);
  }

  @Override
  public Iterator<MachineBuilderProperty> iterator() {
    return getBuilderProperties(container).stream()
        .map(entry -> new MachineBuilderProperty(entry.getKey(), entry.getValue()))
        .iterator();
  }
}
