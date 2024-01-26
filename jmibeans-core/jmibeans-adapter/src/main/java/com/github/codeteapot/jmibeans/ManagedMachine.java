package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

class ManagedMachine implements Machine, ManagedMachineStateChanger {

  private ManagedMachineState state;

  ManagedMachine(MachineRef ref, PlatformEventTarget eventTarget) {
    this(stateChanger -> new ManagedMachineNewState(stateChanger, ref, eventTarget));
  }

  ManagedMachine(ManagedMachineStateMapper stateMapper) {
    state = stateMapper.map(this);
  }

  @Override
  public <F> Optional<F> getFacet(Class<F> type) {
    return state.getFacet(type);
  }

  // TODO Covered on PlatformAdapterAcceptanceTest
  @Override
  public void changeState(ManagedMachineState newState) {
    state = requireNonNull(newState);
  }

  void build(
      Consumer<MachineRef> removalAction,
      ExecutorService builderExecutor,
      MachineBuilder builder,
      MachineAgent agent) {
    state.build(removalAction, builderExecutor, builder, agent);
  }

  boolean isReady() {
    return state.isReady();
  }

  void dispose() {
    state.dispose();
  }
}
