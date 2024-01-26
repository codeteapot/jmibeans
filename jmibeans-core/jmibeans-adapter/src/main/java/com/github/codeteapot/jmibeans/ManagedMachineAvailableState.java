package com.github.codeteapot.jmibeans;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.profile.MachineDisposeAction;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

class ManagedMachineAvailableState extends ManagedMachineState {

  private static final Logger logger = getLogger(ManagedMachine.class.getName());

  private final PlatformEventTarget eventTarget;
  private final Set<Object> facets;
  private final Set<MachineDisposeAction> disposeActions;

  ManagedMachineAvailableState(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      PlatformEventTarget eventTarget,
      Set<Object> facets,
      Set<MachineDisposeAction> disposeActions) {
    super(stateChanger, ref);
    this.eventTarget = requireNonNull(eventTarget);
    this.facets = unmodifiableSet(facets);
    this.disposeActions = unmodifiableSet(disposeActions);
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
  public void dispose() {
    stateChanger.disposed(ref);
    eventTarget.fireLost(new MachineLostEvent(this, ref));
    disposeActions.forEach(this::tryDispose);
  }

  private void tryDispose(MachineDisposeAction action) {
    try {
      action.dispose();
    } catch (Exception e) {
      logger.log(WARNING, "Error occurred while executing dispose task", e);
    }
  }
}
