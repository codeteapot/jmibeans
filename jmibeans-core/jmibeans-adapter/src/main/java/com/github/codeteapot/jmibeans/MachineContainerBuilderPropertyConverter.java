package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

class MachineContainerBuilderPropertyConverter {

  private final Class<?> type;
  private final MachineBuilderPropertyConverter<?> implementation;

  <T> MachineContainerBuilderPropertyConverter(
      Class<T> type,
      MachineBuilderPropertyConverter<T> implementation) {
    this.type = requireNonNull(type);
    this.implementation = requireNonNull(implementation);
  }

  boolean match(Class<?> type) {
    return type.isAssignableFrom(this.type);
  }

  <T> T convert(String rawValue, Class<T> type) throws Exception {
    return type.cast(implementation.convert(rawValue));
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof MachineContainerBuilderPropertyConverter) {
      MachineContainerBuilderPropertyConverter converter =
          (MachineContainerBuilderPropertyConverter) obj;
      return type.equals(converter.type);
    }
    return false;
  }
}
