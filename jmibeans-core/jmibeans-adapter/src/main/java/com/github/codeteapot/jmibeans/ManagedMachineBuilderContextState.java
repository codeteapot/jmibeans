package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import java.util.Set;

abstract class ManagedMachineBuilderContextState {

  protected final ManagedMachineBuilderContextStateChanger stateChanger;
  protected final Set<Runnable> disposeTasks;

  protected ManagedMachineBuilderContextState(
      ManagedMachineBuilderContextStateChanger stateChanger,
      Set<Runnable> disposeTasks) {
    this.stateChanger = requireNonNull(stateChanger);
    this.disposeTasks = requireNonNull(disposeTasks);
  }

  abstract MachineAgent getAgent();

  abstract Set<Runnable> getDisposeTasks();

  abstract void addDisposeTask(Runnable task);

  abstract void finish();
}
