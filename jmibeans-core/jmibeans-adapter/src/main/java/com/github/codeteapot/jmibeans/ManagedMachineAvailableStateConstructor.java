package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineDisposeAction;
import java.util.Set;

@FunctionalInterface
interface ManagedMachineAvailableStateConstructor {

  ManagedMachineAvailableState construct(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      PlatformEventTarget eventTarget,
      Set<Object> facets,
      Set<MachineDisposeAction> disposeActions);
}
