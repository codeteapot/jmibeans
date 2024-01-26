package com.github.codeteapot.jmibeans.platform;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Access to the facets of a machine available on the platform.
 */
public interface Machine {

  /**
   * Access the facet for the specified class.
   *
   * @param <F> Facet type.
   *
   * @param type The facet type class.
   *
   * @return The instance of the facet or {@code empty} if a facet of this type has not been
   *         registered.
   */
  <F> Optional<F> getFacet(Class<F> type);

  /**
   * Function to get a facet for the specified class.
   *
   * <p>Example usage in conjunction with {@link PlatformContext#lookup(MachineRef)},
   * <pre>
   * context.lookup(machineRef)
   *     .flatMap(facetGet(Foo.class))
   *     .ifPresent(foo -&gt; foo.doSomething());
   * </pre>
   *
   * @param <F> Facet type.
   *
   * @param type The facet type class.
   *
   * @return The get function.
   */
  static <F> Function<? super Machine, Optional<F>> facetGet(Class<F> type) {
    return machine -> machine.getFacet(type);
  }

  /**
   * Function to filter facets of the specified class.
   *
   * <p>Example of use in conjunction with {@link PlatformContext#available()},
   * <pre>
   * context.available()
   *     .flatMap(facetFilter(Foo.class))
   *     .forEach(foo -&gt; foo.doSomething());
   * </pre>
   *
   * @param <F> Facet type.
   *
   * @param type The facet type class.
   *
   * @return The filter function.
   */
  static <F> Function<? super Machine, Stream<F>> facetFilter(Class<F> type) {
    return machine -> machine.getFacet(type).map(Stream::of).orElseGet(Stream::empty);
  }
}
