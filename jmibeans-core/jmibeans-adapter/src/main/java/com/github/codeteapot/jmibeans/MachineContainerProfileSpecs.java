package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineBuilderPropertyResolver;

class MachineContainerProfileSpecs {

  final MachineRef ref;
  final MachineBuilderPropertyResolver builderPropertyResolver;
  final MachineAgent agent;

  MachineContainerProfileSpecs(
      MachineRef ref,
      MachineBuilderPropertyResolver builderPropertyResolver,
      MachineAgent agent) {
    this.ref = requireNonNull(ref);
    this.agent = requireNonNull(agent);
    this.builderPropertyResolver = requireNonNull(builderPropertyResolver);
  }
}
