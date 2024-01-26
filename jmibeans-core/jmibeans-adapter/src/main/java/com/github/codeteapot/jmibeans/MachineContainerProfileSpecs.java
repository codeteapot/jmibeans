package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperties;

class MachineContainerProfileSpecs {

  final MachineRef ref;
  final MachineBuilderProperties builderProperties;
  final MachineAgent agent;

  MachineContainerProfileSpecs(
      MachineRef ref,
      MachineBuilderProperties builderProperties,
      MachineAgent agent) {
    this.ref = requireNonNull(ref);
    this.agent = requireNonNull(agent);
    this.builderProperties = requireNonNull(builderProperties);
  }
}
