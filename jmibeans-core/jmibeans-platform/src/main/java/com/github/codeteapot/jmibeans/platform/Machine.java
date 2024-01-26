package com.github.codeteapot.jmibeans.platform;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Machine {

  <F> Optional<F> getFacet(Class<F> type);

  static <F> Function<Machine, Optional<F>> facetGet(Class<F> type) {
    return machine -> machine.getFacet(type);
  }

  static <F> Function<Machine, Stream<F>> facetFilter(Class<F> type) {
    return machine -> machine.getFacet(type).map(Stream::of).orElseGet(Stream::empty);
  }
}
