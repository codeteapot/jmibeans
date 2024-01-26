package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import java.util.Set;

@FunctionalInterface
interface ManagedMachineAvailableStateConstructor {

  ManagedMachineAvailableState construct(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      PlatformEventTarget eventTarget,
      Set<Object> facets);
}
