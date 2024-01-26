package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

abstract class ManagedMachineState {

  protected final ManagedMachineStateChanger stateChanger;
  protected final MachineRef ref;

  ManagedMachineState(ManagedMachineStateChanger stateChanger, MachineRef ref) {
    this.stateChanger = requireNonNull(stateChanger);
    this.ref = requireNonNull(ref);
  }

  abstract <F> Optional<F> getFacet(Class<F> type);

  abstract boolean isReady();

  abstract void build(
      Consumer<MachineRef> removalAction,
      ExecutorService builderExecutor,
      MachineBuilder builder,
      MachineAgent agent);

  abstract void dispose();
}
