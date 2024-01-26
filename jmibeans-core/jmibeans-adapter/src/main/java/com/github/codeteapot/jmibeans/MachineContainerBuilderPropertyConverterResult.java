package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import java.util.Optional;

class MachineContainerBuilderPropertyConverterResult<T> {

  private final T value;
  private final Exception exception;

  MachineContainerBuilderPropertyConverterResult() {
    value = null;
    exception = null;
  }

  MachineContainerBuilderPropertyConverterResult(T value) {
    this.value = requireNonNull(value);
    exception = null;
  }

  MachineContainerBuilderPropertyConverterResult(Exception exception) {
    value = null;
    this.exception = requireNonNull(exception);
  }

  Optional<T> getValue() throws Exception {
    if (exception != null)
      throw exception;
    return ofNullable(value);
  }
}
