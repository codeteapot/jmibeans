package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineBuilderPropertyResolver;

@FunctionalInterface
interface ManagedMachineConstructor {

  ManagedMachine construct(
      MachineRef ref,
      PlatformEventTarget eventTarget,
      MachineBuilderPropertyResolver builderPropertyResolver,
      MachineAgent agent,
      ManagedMachineBuildingJob builderJob);
}
