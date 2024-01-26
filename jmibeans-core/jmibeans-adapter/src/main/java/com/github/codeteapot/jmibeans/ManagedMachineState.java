package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.ReferencedMachine;
import java.util.Optional;

abstract class ManagedMachineState {

  protected final ManagedMachineStateChanger stateChanger;
  protected final MachineRef ref;

  protected ManagedMachineState(ManagedMachineStateChanger stateChanger, MachineRef ref) {
    this.stateChanger = requireNonNull(stateChanger);
    this.ref = requireNonNull(ref);
  }

  abstract <F> Optional<F> getFacet(Class<F> type);

  abstract boolean isReady();

  abstract void dispose();

  ReferencedMachine reference(Machine machine) {
    return new ReferencedMachineImpl(ref, machine);
  }
}
