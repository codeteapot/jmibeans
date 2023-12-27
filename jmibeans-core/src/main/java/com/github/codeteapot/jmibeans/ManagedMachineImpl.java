package com.github.codeteapot.jmibeans;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.machine.Disposable;
import com.github.codeteapot.jmibeans.machine.MachineFacet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

class ManagedMachineImpl implements ManagedMachine {

  private static final Logger logger = getLogger(ManagedMachineImpl.class.getName());

  private final MachineSessionPoolReleaser sessionPoolReleaser;
  private final Set<MachineFacet> facets;

  ManagedMachineImpl(MachineSessionPoolReleaser sessionPoolReleaser, Set<MachineFacet> facets) {
    this.sessionPoolReleaser = requireNonNull(sessionPoolReleaser);
    this.facets = unmodifiableSet(facets);
  }

  @Override
  public <F extends MachineFacet> Optional<F> getFacet(Class<F> type) {
    return facets.stream()
        .filter(facet -> type.isAssignableFrom(facet.getClass()))
        .map(type::cast)
        .findAny();
  }

  @Override
  public void dispose() {
    facets.stream()
        .filter(Disposable.class::isInstance)
        .map(Disposable.class::cast)
        .forEach(this::tryDispose);
    sessionPoolReleaser.releaseAll();
  }

  private void tryDispose(Disposable disposable) {
    try {
      disposable.dispose();
    } catch (RuntimeException e) {
      logger.log(WARNING, "Error occurred while disposing facet", e);
    }
  }
}
