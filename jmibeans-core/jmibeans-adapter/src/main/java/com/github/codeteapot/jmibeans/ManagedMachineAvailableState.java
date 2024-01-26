package com.github.codeteapot.jmibeans;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.profile.Disposable;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

class ManagedMachineAvailableState extends ManagedMachineState {

  private static final Logger logger = getLogger(ManagedMachineAvailableState.class.getName());

  private final PlatformEventTarget eventTarget;
  private final Set<Object> facets;
  private final ManagedMachineDisposedStateConstructor disposedStateConstructor;

  ManagedMachineAvailableState(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      PlatformEventTarget eventTarget,
      Set<Object> facets) {
    this(stateChanger, ref, eventTarget, facets, ManagedMachineDisposedState::new);
  }

  ManagedMachineAvailableState(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      PlatformEventTarget eventTarget,
      Set<Object> facets,
      ManagedMachineDisposedStateConstructor disposedStateConstructor) {
    super(stateChanger, ref);
    this.eventTarget = requireNonNull(eventTarget);
    this.facets = unmodifiableSet(facets);
    this.disposedStateConstructor = requireNonNull(disposedStateConstructor);
  }

  @Override
  public <F> Optional<F> getFacet(Class<F> type) {
    return facets.stream()
        .filter(facet -> type.isAssignableFrom(facet.getClass()))
        .map(type::cast)
        .findAny();
  }

  @Override
  boolean isReady() {
    return true;
  }

  @Override
  void build(
      Consumer<MachineRef> removalAction,
      ExecutorService builderExecutor,
      MachineBuilder builder,
      MachineAgent agent) {
    throw new IllegalStateException("The machine has already been built");
  }

  @Override
  public void dispose() {
    stateChanger.changeState(disposedStateConstructor.construct(stateChanger, ref));
    eventTarget.fireLostEvent(new MachineLostEvent(this, ref));
    facets.stream()
        .filter(Disposable.class::isInstance)
        .map(Disposable.class::cast)
        .forEach(this::tryDispose);
  }

  private void tryDispose(Disposable disposable) {
    try {
      disposable.dispose();
    } catch (RuntimeException e) {
      logger.log(WARNING, "Error occurred while disposing facet", e);
    }
  }
}
