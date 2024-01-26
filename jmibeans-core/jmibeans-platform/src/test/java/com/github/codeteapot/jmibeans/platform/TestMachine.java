package com.github.codeteapot.jmibeans.platform;

import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

class TestMachine implements Machine {

  private final Set<Object> facets;

  TestMachine(Object... facets) {
    this.facets = Stream.of(facets).collect(toSet());
  }

  @Override
  public <F> Optional<F> getFacet(Class<F> type) {
    return facets.stream()
        .filter(facet -> type.isAssignableFrom(facet.getClass()))
        .map(type::cast)
        .findAny();
  }
}
