package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineFacet;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Machine available in the platform context.
 *
 * <p>A machine is implicitly uniquely referenced. All of its facets return that reference via the
 * {@link MachineFacet#getRef()} method.
 *
 * <p>Its facets determine the operations that can be performed. Each facet implements a particular
 * interface, and there cannot be more than one implementing the same. So there can't be more than
 * one facet instance of the same type and with the same reference.
 *
 * <p>In this way, this concept is abstracted from the machine's proprietary infrastructure.
 *
 * @see PlatformContext
 */
public interface Machine {

  /**
   * Returns the facet of the specified type, if any.
   *
   * @param <F> The type implemented by the facet.
   *
   * @param type The class of the type implemented by the facet.
   *
   * @return The {@link Optional} with the facet that implements the specified type. Or empty if
   *         there isn't.
   */
  <F extends MachineFacet> Optional<F> getFacet(Class<F> type);

  /**
   * Function that, given a machine and a facet type, returns an {@link Optional} with a machine if
   * that type is implemented. Or an empty {@link Optional} otherwise.
   *
   * <p>Intended to be used with {@link Optional#flatMap(Function)},
   * <pre>
   * context.lookup(ref)
   *   .flatMap(facetGet(SomeMachineFacet.class))
   *   .ifPresent(SomeMachineFacet::doSomething);
   * </pre>
   * Taking into account that
   * {@link PlatformContext#lookup(com.github.codeteapot.jmibeans.machine.MachineRef)} returns an
   * {@link Optional} object of type {@code Machine}.
   *
   * @param <F> The type that should implement the facet.
   *
   * @param type The class of the type that should implement the facet.
   *
   * @return The {@link Optional} with the facet that implements the specified type, for the
   *         specified machine. Or empty if there isn't.
   */
  static <F extends MachineFacet> Function<Machine, Optional<F>> facetGet(Class<F> type) {
    return machine -> machine.getFacet(type);
  }

  /**
   * Function that, given a machine and a facet type, returns a {@link Stream} with a machine if
   * that type is implemented. Or an empty {@link Stream} otherwise.
   *
   * <p>Intended to be used with {@link Stream#flatMap(Function)},
   * <pre>
   * context.available()
   *   .flatMap(facetFilter(SomeMachineFacet.class))
   *   .forEach(SomeMachineFacet::doSomething);
   * </pre>
   * Taking into account that {@link PlatformContext#available()} returns a {@link Stream} of
   * objects of type {@code Machine}.
   *
   * @param <F> The type that should implement the facet.
   *
   * @param type The class of the type that should implement the facet.
   *
   * @return The {@link Stream} with the facet that implements the specified type, for the specified
   *         machine. Or empty if there isn't.
   */
  static <F extends MachineFacet> Function<Machine, Stream<F>> facetFilter(Class<F> type) {
    return machine -> machine.getFacet(type).map(Stream::of).orElseGet(Stream::empty);
  }
}
