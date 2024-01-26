package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import java.util.Optional;

class ManagedMachineDisposedState extends ManagedMachineState {

  ManagedMachineDisposedState(ManagedMachineStateChanger stateChanger, MachineRef ref) {
    super(stateChanger, ref);
  }

  @Override
  public <F> Optional<F> getFacet(Class<F> type) {
    throw new IllegalStateException("The machine has been disposed");
  }

  @Override
  public boolean isReady() {
    return false;
  }

  @Override
  public void dispose() {
    throw new IllegalStateException("The machine has already been disposed");
  }
}
