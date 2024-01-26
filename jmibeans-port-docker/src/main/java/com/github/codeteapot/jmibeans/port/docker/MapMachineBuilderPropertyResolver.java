package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.port.MachineBuilderPropertyResolver;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

class MapMachineBuilderPropertyResolver implements MachineBuilderPropertyResolver {

  private final Map<String, Set<String>> propertyMap;

  MapMachineBuilderPropertyResolver(Map<String, Set<String>> propertyMap) {
    this.propertyMap = requireNonNull(propertyMap);
  }

  @Override
  public Set<String> getProperty(String name) {
    return ofNullable(propertyMap.get(name))
        .map(Collections::unmodifiableSet)
        .orElseGet(Collections::emptySet);
  }

}
