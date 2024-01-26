package com.github.codeteapot.jmibeans.profile;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;

public class MachineBuilderResultDefinition implements MachineBuilderResult {

  private final Set<Object> facets;

  public MachineBuilderResultDefinition() {
    facets = new HashSet<>();
  }

  @Override
  public Set<Object> getFacets() {
    return facets;
  }

  public MachineBuilderResultDefinition withFacet(Object facet) {
    facets.add(requireNonNull(facet));
    return this;
  }
}
