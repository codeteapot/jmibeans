package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;

class ManagedMachineBuildingState extends ManagedMachineState implements MachineBuilderContext {

  private static final Logger logger = getLogger(ManagedMachineBuildingState.class.getName());

  private final Consumer<MachineRef> removalAction;
  private final MachineAgent agent;
  private final Set<Object> facets;
  private final Future<Void> task;
  private final ManagedMachineAvailableStateConstructor availableStateConstructor;
  private final ManagedMachineDisposedStateConstructor disposedStateConstructor;

  ManagedMachineBuildingState(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      Consumer<MachineRef> removalAction,
      PlatformEventTarget eventTarget,
      ExecutorService builderExecutor,
      MachineBuilder builder,
      MachineAgent agent) {
    this(
        stateChanger,
        ref,
        removalAction,
        agent,
        eventTarget,
        new HashSet<>(),
        builderExecutor,
        builder,
        ManagedMachineAvailableState::new,
        ManagedMachineDisposedState::new);
  }

  ManagedMachineBuildingState(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      Consumer<MachineRef> removalAction,
      MachineAgent agent,
      PlatformEventTarget eventTarget,
      Set<Object> facets,
      ExecutorService builderExecutor,
      MachineBuilder builder,
      ManagedMachineAvailableStateConstructor availableStateConstructor,
      ManagedMachineDisposedStateConstructor disposedStateConstructor) {
    super(stateChanger, ref);
    this.removalAction = requireNonNull(removalAction);
    this.agent = requireNonNull(agent);
    this.facets = requireNonNull(facets);
    this.availableStateConstructor = requireNonNull(availableStateConstructor);
    this.disposedStateConstructor = requireNonNull(disposedStateConstructor);
    task = builderExecutor.submit(() -> build(eventTarget, builder));
  }

  @Override
  public void registerFacet(Object facet) {
    facets.add(facet);
  }

  @Override
  public MachineAgent getAgent() {
    return agent;
  }

  @Override
  <F> Optional<F> getFacet(Class<F> type) {
    throw new IllegalStateException("The machine is still being built");
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
    throw new IllegalStateException("The machine is already being built");
  }

  @Override
  void dispose() {
    stateChanger.changeState(disposedStateConstructor.construct(stateChanger, ref));
    task.cancel(true);
    removalAction.accept(ref);
  }

  private Void build(PlatformEventTarget eventTarget, MachineBuilder builder) {
    try {
      builder.build(this);
      stateChanger.changeState(availableStateConstructor.construct(
          stateChanger,
          ref,
          eventTarget,
          facets));
      eventTarget.fireAvailableEvent(new MachineAvailableEvent(this, ref));
      return null;
    } catch (MachineBuildingException | RuntimeException e) {
      removalAction.accept(ref);
      logger.log(SEVERE, new StringBuilder()
          .append("Machine ").append(ref).append(" build failed")
          .toString(), e);
      return null;
    }
  }
}
