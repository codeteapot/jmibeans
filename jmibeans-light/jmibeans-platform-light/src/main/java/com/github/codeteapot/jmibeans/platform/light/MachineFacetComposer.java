package com.github.codeteapot.jmibeans.platform.light;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;

/**
 * Ease of creating a composition of facets available on a machine.
 *
 * @param <T> Instance type of the current composition.
 *
 * @see Machine
 */
public final class MachineFacetComposer<T> {

  private final Machine machine;
  private final T composition;

  private MachineFacetComposer(Machine machine, T composition) {
    this.machine = requireNonNull(machine);
    this.composition = composition;
  }

  /**
   * Determine if the composition instance is null.
   *
   * @return {@code true} if the instance is {@code null}; {@code false} otherwise.
   */
  public boolean isEmpty() {
    return composition == null;
  }

  /**
   * Combine the current composition instance with the facet of the specified type.
   *
   * <p>
   * It does nothing if the composition is empty. If the machine does not have the facet of the
   * specified type, it returns an empty composition.
   *
   * <p>
   * The result of the composition can be {@code null}, resulting in an empty composition.
   *
   * @param <R> Type of the resulting composition instance.
   * @param <F> Type of facet to combine.
   *
   * @param combiner Function that combines the current composition instance with the obtained
   *        facet.
   * @param type Specification of the type of facet to be combined.
   *
   * @return The composition obtained.
   *
   * @see Machine#getFacet(Class)
   */
  public <R, F> MachineFacetComposer<R> map(BiFunction<T, F, R> combiner, Class<F> type) {
    return new MachineFacetComposer<>(machine, composition == null
        ? null
        : machine.getFacet(type).map(facet -> combiner.apply(composition, facet)).orElse(null));
  }

  /**
   * Obtains the optional instance.
   *
   * <p>
   * The finisher result can be {@code null}, resulting in an empty optional.
   *
   * <p>
   * Note that the call to {@link #isEmpty()} returns the same as the call to
   * {@link Optional#isEmpty()} of the returned value.
   *
   * @param <R> Type of the returned instance, final result of the composition.
   *
   * @param finisher Finalizing function to the resulting type.
   *
   * @return The optional instance resulting from the composition.
   */
  public <R> Optional<R> get(Function<T, R> finisher) {
    return ofNullable(composition).map(finisher);
  }

  /**
   * Begin the composition of facets of a machine.
   *
   * <p>
   * If the supplier returns {@code null}, an empty composition is created.
   *
   * @param <T> Initial type of composition.
   *
   * @param machine The machine whose facets are going to be composed.
   * @param compositionSupplier Supplier of the initial instance of the composition.
   *
   * @return The resulting initial composition.
   */
  public static <T> MachineFacetComposer<T> of(Machine machine, Supplier<T> compositionSupplier) {
    return new MachineFacetComposer<>(machine, compositionSupplier.get());
  }

  /**
   * Function on a machine to initiate facet composition on it.
   *
   * <p>
   * Use the {@link #of(Machine, Supplier)} operation using the machine involved as the parameter of
   * the function.
   *
   * <p>
   * Examples taking into account the following constructor,
   * 
   * <pre>
   * class InitialMachineFacetComposition {
   * 
   *   InitialMachineFacetComposition() { ... }
   * }
   * </pre>
   * 
   * <p>
   * In conjunction with {@link PlatformContext#lookup(MachineRef)},
   * 
   * <pre>
   * context.lookup(event.getMachineRef())
   *     .map(composeFacet(InitialMachineFacetComposition::new))
   *     ...
   * </pre>
   * 
   * <p>
   * In conjunction with {@link PlatformContext#available()},
   * 
   * <pre>
   * context.available()
   *     .map(composeFacet(InitialMachineFacetComposition::new))
   *     ...
   * </pre>
   *
   * @param <T> Initial type of composition.
   *
   * @param compositionSupplier Supplier of the initial instance of the composition.
   *
   * @return The function to create the initial composition.
   */
  public static <T> Function<Machine, MachineFacetComposer<T>> composeFacet(
      Supplier<T> compositionSupplier) {
    return machine -> MachineFacetComposer.of(machine, compositionSupplier);
  }

  /**
   * Function on a composition that creates a new composition by combining a facet.
   *
   * <p>
   * Apply the {@link #map(BiFunction, Class)} operation using the current composition as the
   * parameter of the function.
   *
   * <p>
   * Examples taking into account the following constructors,
   * 
   * <pre>
   * class FooMachineFacetComposition {
   * 
   *   FooMachineFacetComposition(InitialMachineFacetComposition comp, FooFacet facet) { ... }
   * }
   * 
   * class FooBarMachineFacetComposition {
   * 
   *   FooBarMachineFacetComposition(FooMachineFacetComposition comp, BarFacet facet) { ... }
   * }
   * </pre>
   * 
   * <p>
   * In conjunction with {@link PlatformContext#lookup(MachineRef)} and with
   * {@link #composeFacet(Supplier)},
   *
   * <pre>
   * context.lookup(event.getMachineRef())
   *     .map(composeFacet(InitialMachineFacetComposition::new))
   *     .map(composeMap(FooMachineFacetComposition::new, FooFacet.class))
   *     .map(composeMap(FooBarMachineFacetComposition::new, BarFacet.class))
   *     ...
   * </pre>
   *
   * <p>
   * In conjunction with {@link PlatformContext#available()} and with
   * {@link #composeFacet(Supplier)},
   *
   * <pre>
   * context.available()
   *     .map(composeFacet(InitialMachineFacetComposition::new))
   *     .map(composeMap(FooMachineFacetComposition::new, FooFacet.class))
   *     .map(composeMap(FooBarMachineFacetComposition::new, BarFacet.class))
   *     ...
   * </pre>
   *
   * @param <T> Type of the resulting composition instance.
   * @param <R> Type of the returned instance, final result of the composition.
   * @param <F> Type of facet to combine.
   *
   * @param combiner Function that combines the current composition instance with the obtained
   *        facet.
   * @param type Specification of the type of facet to be combined.
   *
   * @return The function of combining the composition with the specified facet.
   */
  public static <T, R, F> Function< //
      MachineFacetComposer<T>, //
      MachineFacetComposer<R>> composeMap(BiFunction<T, F, R> combiner, Class<F> type) {
    return composer -> composer.map(combiner, type);
  }

  /**
   * Function for obtaining the result.
   *
   * <p>
   * Apply the {@link #get(Function)} operation using the current composition as the parameter of
   * the function.
   *
   * <p>
   * Example taking into account the following constructor,
   * 
   * <pre>
   * class ResultMachineFacetComposition {
   * 
   *   ResultMachineFacetComposition(FooBarMachineFacetComposition comp) { ... }
   * }
   * </pre>
   *
   * <p>
   * In conjunction with {@link PlatformContext#lookup(MachineRef)}, with
   * {@link #composeFacet(Supplier)} and {@link #composeMap(BiFunction, Class)},
   *
   * <pre>
   * context.lookup(event.getMachineRef())
   *     .map(composeFacet(InitialMachineFacetComposition::new))
   *     .map(composeMap(FooMachineFacetComposition::new, FooFacet.class))
   *     .map(composeMap(FooBarMachineFacetComposition::new, BarFacet.class))
   *     .flatMap(composeGet(ResultMachineFacetComposition::new))
   *     .ifPresent(resultComp -> ...);
   * </pre>
   *
   * @param <T> Type of the resulting composition instance.
   * @param <R> Type of the returned instance, final result of the composition.
   *
   * @param finisher Finalizing function to the resulting type.
   *
   * @return The function to obtain the optional with the result.
   */
  public static <T, R> Function<MachineFacetComposer<T>, Optional<R>> composeGet(
      Function<T, R> finisher) {
    return composer -> composer.get(finisher);
  }

  /**
   * Function for obtaining the result in stream.
   *
   * <p>
   * Apply the {@link #get(Function)} operation using the current composition as the parameter of
   * the function.
   *
   * <p>
   * This is a convenience operation for streams.
   * 
   * <p>
   * Example taking into account the following constructor,
   * 
   * <pre>
   * class ResultMachineFacetComposition {
   * 
   *   ResultMachineFacetComposition(FooBarMachineFacetComposition comp) { ... }
   * }
   * </pre>
   *
   * <p>
   * In conjunction with {@link PlatformContext#available()}, with {@link #composeFacet(Supplier)}
   * and {@link #composeMap(BiFunction, Class)},
   *
   * <pre>
   * context.available()
   *     .map(composeFacet(InitialMachineFacetComposition::new))
   *     .map(composeMap(FooMachineFacetComposition::new, FooFacet.class))
   *     .map(composeMap(FooBarMachineFacetComposition::new, BarFacet.class))
   *     .flatMap(composeFilter(ResultMachineFacetComposition::new))
   *     .forEach(resultComp -> ...);
   * </pre>
   *
   * @param <T> Type of the resulting composition instance.
   * @param <R> Type of the returned instance, final result of the composition.
   *
   * @param finisher Finalizing function to the resulting type.
   *
   * @return The function to obtain the stream with the result.
   */
  public static <T, R> Function<MachineFacetComposer<T>, Stream<R>> composeFilter(
      Function<T, R> finisher) {
    return composer -> composer.get(finisher).map(Stream::of).orElseGet(Stream::empty);
  }
}
