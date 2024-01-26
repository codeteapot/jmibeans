package com.github.codeteapot.jmibeans;

@FunctionalInterface
interface ManagedMachineBuilderPropertyValueConstructor {

  ManagedMachineBuilderPropertyValue construct(
      MachineContainerBuilderPropertyConverters converters,
      String rawValue);
}
