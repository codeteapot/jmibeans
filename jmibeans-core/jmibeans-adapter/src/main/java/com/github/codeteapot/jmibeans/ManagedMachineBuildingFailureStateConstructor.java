package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.platform.MachineRef;

@FunctionalInterface
interface ManagedMachineBuildingFailureStateConstructor {

  ManagedMachineBuildingFailureState construct(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref);
}
