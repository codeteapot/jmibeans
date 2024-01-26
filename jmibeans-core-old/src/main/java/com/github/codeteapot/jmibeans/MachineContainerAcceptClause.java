package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineLink;

@FunctionalInterface
interface MachineContainerAcceptClause {

  void accept(MachineRef ref, MachineLink link) throws InterruptedException;
}
