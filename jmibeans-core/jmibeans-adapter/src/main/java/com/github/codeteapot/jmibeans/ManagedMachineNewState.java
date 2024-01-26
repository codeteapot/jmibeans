package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

class ManagedMachineNewState extends ManagedMachineState {

  private final PlatformEventTarget eventTarget;
  private final ManagedMachineBuildingStateConstructor buildingStateConstructor;

  ManagedMachineNewState(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      PlatformEventTarget eventTarget) {
    this(stateChanger, ref, eventTarget, ManagedMachineBuildingState::new);
  }

  ManagedMachineNewState(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      PlatformEventTarget eventTarget,
      ManagedMachineBuildingStateConstructor buildingStateConstructor) {
    super(stateChanger, ref);
    this.eventTarget = requireNonNull(eventTarget);
    this.buildingStateConstructor = requireNonNull(buildingStateConstructor);
  }

  @Override
  <F> Optional<F> getFacet(Class<F> type) {
    throw new IllegalStateException("The machine has not yet begun to be built");
  }

  @Override
  boolean isReady() {
    return false;
  }

  @Override
  void build(
      Consumer<MachineRef> removalAction,
      ExecutorService builderExecutor,
      MachineBuilder builder,
      MachineAgent agent) {
    stateChanger.changeState(buildingStateConstructor.construct(
        stateChanger,
        ref,
        removalAction,
        eventTarget,
        builderExecutor,
        builder,
        agent));
  }

  @Override
  void dispose() {
    throw new IllegalStateException("The machine has not yet begun to be built");
  }
}
