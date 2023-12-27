package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineContext;

@FunctionalInterface
interface ManagedMachineFactoryConstructor {

  ManagedMachineFactory construct(MachineContext context);
}
