package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.ReferencedMachine;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

// TODO DESIGN Using ManagedMachineFactory and public ManagedMachine
// and DefaultManagedMachineFactory
class ManagedMachine implements Machine {

  private ManagedMachineState state;

  ManagedMachine(
      MachineRef ref,
      PlatformEventTarget eventTarget,
      MachineAgent agent,
      ManagedMachineBuildingJob buildingJob) {
    this(changeStateAction -> initial(
        changeStateAction,
        ref,
        eventTarget,
        agent,
        buildingJob));
  }

  ManagedMachine(Function<Consumer<ManagedMachineState>, ManagedMachineState> stateMapper) {
    state = stateMapper.apply(newState -> state = requireNonNull(newState));
  }

  @Override
  public <F> Optional<F> getFacet(Class<F> type) {
    return state.getFacet(type);
  }

  boolean isReady() {
    return state.isReady();
  }

  ReferencedMachine referenced() {
    return state.reference(this);
  }

  void dispose() {
    state.dispose();
  }

  private static ManagedMachineState initial(
      Consumer<ManagedMachineState> changeStateAction,
      MachineRef ref,
      PlatformEventTarget eventTarget,
      MachineAgent agent,
      ManagedMachineBuildingJob buildingJob) {
    return new ManagedMachineBuildingState(
        new ManagedMachineStateChanger(
            changeStateAction,
            ManagedMachineAvailableState::new,
            ManagedMachineBuildingFailureState::new,
            ManagedMachineDisposedState::new),
        ref,
        eventTarget,
        agent,
        buildingJob);
  }
}
