package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.machine.MachineRef;

@FunctionalInterface
interface MachineLostEventConstructor {

  MachineLostEvent construct(Object source, MachineRef machineRef);
}
