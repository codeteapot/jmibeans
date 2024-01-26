package com.github.codeteapot.jmibeans;

import static java.lang.Thread.currentThread;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.profile.MachineBuildingResult;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.logging.Logger;

class ManagedMachineBuildingState extends ManagedMachineState {

  private static final Logger logger = getLogger(ManagedMachine.class.getName());

  private final PlatformEventTarget eventTarget;
  private final MachineAgent agent;
  private final Future<Void> task;

  ManagedMachineBuildingState(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      PlatformEventTarget eventTarget,
      MachineAgent agent,
      ManagedMachineBuildingJob buildingJob) {
    super(stateChanger, ref);
    this.eventTarget = requireNonNull(eventTarget);
    this.agent = requireNonNull(agent);
    task = requireNonNull(buildingJob.submit(this::build));
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
      ManagedMachineBuilderContext context = new ManagedMachineBuilderContext(agent);
      MachineBuildingResult result = builder.build(context); // TODO Check null result
      context.finish();
      runIfNotInterrupted(() -> {
        stateChanger.available(ref, eventTarget, result.getFacets(), context.getDisposeTasks());
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
