package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineFacet;
import java.util.Set;

@FunctionalInterface
interface ManagedMachineConstructor {

  ManagedMachine construct(
      MachineSessionPoolReleaser sessionPoolReleaser,
      Set<MachineFacet> facets);
}
