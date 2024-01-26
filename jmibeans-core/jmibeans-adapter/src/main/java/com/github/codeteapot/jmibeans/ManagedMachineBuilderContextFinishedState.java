package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import java.util.Set;

class ManagedMachineBuilderContextFinishedState extends ManagedMachineBuilderContextState {

  ManagedMachineBuilderContextFinishedState(
      ManagedMachineBuilderContextStateChanger stateChanger,
      Set<Runnable> disposeTasks) {
    super(stateChanger, disposeTasks);
  }

  @Override
  MachineAgent getAgent() {
    throw new IllegalStateException("Already finished");
  }

  @Override
  Set<Runnable> getDisposeTasks() {
    return disposeTasks;
  }

  @Override
  void addDisposeTask(Runnable task) {
    throw new IllegalStateException("Already finished");
  }

  @Override
  void finish() {
    throw new IllegalStateException("Already finished");
  }
}
