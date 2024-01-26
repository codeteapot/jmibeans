package com.github.codeteapot.jmibeans.light;

import static java.util.Objects.requireNonNull;
import com.github.codeteapot.jmibeans.MachineBuilderPropertyConverter;
import com.github.codeteapot.jmibeans.MachineBuilderPropertyConverters;

class MachineCatalogDefinitionBuilderPropertyConverter<T> {

  private final Class<T> type;
  private final MachineBuilderPropertyConverter<T> implementation;

  MachineCatalogDefinitionBuilderPropertyConverter(
      Class<T> type,
      MachineBuilderPropertyConverter<T> implementation) {
    this.type = requireNonNull(type);
    this.implementation = requireNonNull(implementation);
  }

  void registerTo(MachineBuilderPropertyConverters converters) {
    converters.register(type, implementation);
  }
}
