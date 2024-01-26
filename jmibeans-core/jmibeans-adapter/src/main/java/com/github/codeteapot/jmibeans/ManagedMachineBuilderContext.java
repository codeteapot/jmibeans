package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

class ManagedMachineBuilderContext implements MachineBuilderContext {

  private ManagedMachineBuilderContextState state;

  ManagedMachineBuilderContext(MachineAgent agent) {
    this(changeStateAction -> initial(changeStateAction, agent));
  }

  ManagedMachineBuilderContext(Function< //
      Consumer<ManagedMachineBuilderContextState>, //
      ManagedMachineBuilderContextState> stateMapper) {
    state = stateMapper.apply(newState -> state = requireNonNull(newState));
  }

  @Override
  public MachineAgent getAgent() {
    return state.getAgent();
  }

  @Override
  public void addDisposeTask(Runnable task) {
    state.addDisposeTask(task);
  }

  Set<Runnable> getDisposeTasks() {
    return state.getDisposeTasks();
  }

  void finish() {
    state.finish();
  }

  private static ManagedMachineBuilderContextState initial(
      Consumer<ManagedMachineBuilderContextState> changeStateAction,
      MachineAgent agent) {
    return new ManagedMachineBuilderContextActiveState(
        new ManagedMachineBuilderContextStateChanger(
            changeStateAction,
            ManagedMachineBuilderContextFinishedState::new),
        agent);
  }
}
