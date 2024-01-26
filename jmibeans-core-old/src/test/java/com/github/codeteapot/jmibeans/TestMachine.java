package com.github.codeteapot.jmibeans;

import static java.util.stream.Collectors.toSet;

import com.github.codeteapot.jmibeans.Machine;
import com.github.codeteapot.jmibeans.machine.MachineFacet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

class TestMachine implements Machine {

  private final Set<MachineFacet> facets;

  TestMachine(MachineFacet... facets) {
    this.facets = Stream.of(facets).collect(toSet());
  }

  @Override
  public <F extends MachineFacet> Optional<F> getFacet(Class<F> type) {
    return facets.stream()
        .filter(facet -> type.isAssignableFrom(facet.getClass()))
        .map(type::cast)
        .findAny();
  }
}
