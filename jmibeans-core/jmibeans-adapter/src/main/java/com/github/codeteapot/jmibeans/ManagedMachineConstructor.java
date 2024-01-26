package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.platform.MachineRef;

@FunctionalInterface
interface ManagedMachineConstructor {

  ManagedMachine construct(MachineRef ref, PlatformEventTarget eventTarget);
}
