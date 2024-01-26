package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.machine.MachineRef;

@FunctionalInterface
interface MachineAvailableEventConstructor {

  MachineAvailableEvent construct(Object source, MachineRef machineRef);
}
