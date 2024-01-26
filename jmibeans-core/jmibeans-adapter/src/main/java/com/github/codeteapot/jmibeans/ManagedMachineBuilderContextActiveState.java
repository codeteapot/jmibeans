package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import java.util.HashSet;
import java.util.Set;

class ManagedMachineBuilderContextActiveState extends ManagedMachineBuilderContextState {

  private final MachineAgent agent;

  ManagedMachineBuilderContextActiveState(
      ManagedMachineBuilderContextStateChanger stateChanger,
      MachineAgent agent) {
    super(stateChanger, new HashSet<>());
    this.agent = requireNonNull(agent);
  }

  @Override
  MachineAgent getAgent() {
    return agent;
  }

  @Override
  Set<Runnable> getDisposeTasks() {
    throw new IllegalStateException("Not finished");
  }

  @Override
  void addDisposeTask(Runnable task) {
    disposeTasks.add(task);
  }

  @Override
  void finish() {
    stateChanger.finish(disposeTasks);
  }
}
