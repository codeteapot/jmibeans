package com.github.codeteapot.jmibeans;

import static java.lang.Thread.currentThread;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.port.MachineBuilderPropertyResolver;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import com.github.codeteapot.jmibeans.profile.MachineBuilderResult;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.profile.MachineDisposeAction;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Logger;

class ManagedMachineBuildingState extends ManagedMachineState implements MachineBuilderContext {

  private static final Logger logger = getLogger(ManagedMachine.class.getName());

  private final PlatformEventTarget eventTarget;
  private final MachineBuilderPropertyResolver propertyResolver;
  private final MachineAgent agent;
  private final Set<MachineDisposeAction> disposeActions;
  private final Future<Void> task;

  ManagedMachineBuildingState(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      PlatformEventTarget eventTarget,
      MachineBuilderPropertyResolver propertyResolver,
      MachineAgent agent,
      ManagedMachineBuildingJob buildingJob) {
    super(stateChanger, ref);
    this.eventTarget = requireNonNull(eventTarget);
    this.propertyResolver = requireNonNull(propertyResolver);
    this.agent = requireNonNull(agent);
    this.disposeActions = new HashSet<>();
    task = requireNonNull(buildingJob.submit(this::build));
  }

  @Override
  public Set<String> getProperty(String name) {
    return unmodifiableSet(propertyResolver.getProperty(name));
  }

  @Override
  public MachineAgent getAgent() {
    return agent;
  }

  @Override
  public void addDisposeAction(MachineDisposeAction action) {
    disposeActions.add(action);
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
  synchronized void dispose() {
    stateChanger.disposed(ref);
    task.cancel(true);
  }

  private synchronized void runIfNotInterrupted(Runnable action) {
    if (!currentThread().isInterrupted()) {
      action.run();
    }
  }

  private void build(MachineBuilder builder) throws InterruptedException {
    try {
      MachineBuilderResult result = builder.build(this); // TODO Check null result
      runIfNotInterrupted(() -> {
        stateChanger.available(ref, eventTarget, result.getFacets(), disposeActions);
        eventTarget.fireAvailable(new MachineAvailableEvent(this, ref));
      });
    } catch (MachineBuildingException | RuntimeException e) {
      runIfNotInterrupted(() -> {
        stateChanger.buildingFailure(ref);
        logger.log(WARNING, new StringBuilder()
            .append("Machine ").append(ref).append(" build failed")
            .toString(), e);
      });
    }
  }
}
