package com.github.codeteapot.jmibeans;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

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
  void build(
      Consumer<MachineRef> removalAction,
      ExecutorService builderExecutor,
      MachineBuilder builder,
      MachineAgent agent) {
    throw new IllegalStateException("The machine has been disposed");
  }

  @Override
  public void dispose() {
    throw new IllegalStateException("The machine has already been disposed");
  }
}
