package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.platform.MachineRef;

@FunctionalInterface
interface ManagedMachineDisposedStateConstructor {

  ManagedMachineDisposedState construct(ManagedMachineStateChanger stateChanger, MachineRef ref);
}
