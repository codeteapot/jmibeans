package com.github.codeteapot.jmibeans.light;

import static java.util.Optional.ofNullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import com.github.codeteapot.jmibeans.MachineBuilderPropertyConverter;
import com.github.codeteapot.jmibeans.MachineBuilderPropertyConverters;
import com.github.codeteapot.jmibeans.MachineCatalog;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertiesObject;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.profile.MachineBuildingResult;
import com.github.codeteapot.jmibeans.profile.MachineProfile;

/**
 * Programmatic definition of a machine catalog.
 */
public class MachineCatalogDefinition implements MachineCatalog {

  private final Map<MachineProfileName, MachineProfile> profileMap;
  private final Set<MachineCatalogDefinitionBuilderPropertyConverter<?>> converterSet;

  /**
   * Create an empty definition.
   */
  public MachineCatalogDefinition() {
    profileMap = new HashMap<>();
    converterSet = new HashSet<>();
  }

  @Override
  public Optional<MachineProfile> getProfile(MachineProfileName profileName) {
    return ofNullable(profileMap.get(profileName));
  }

  @Override
  public void registerBuilderPropertyConverters(MachineBuilderPropertyConverters converters) {
    converterSet.forEach(converter -> converter.registerTo(converters));
  }

  /**
   * Add a machine builder property converter to register.
   *
   * <p>
   * Example with multiple converters,
   * 
   * <pre>
   * MachineCatalog catalog = new MachineCatalogDefinition()
   *     .builderPropertyConverter(Duration.class, Duration::parse)
   *     .builderPropertyConverter(UUID.class, UUID::fromString);
   * </pre>
   * 
   * @param <T> Type handled by the converter.
   *
   * @param type Type for which the converter will be registered.
   * @param converter The converter to be registered.
   *
   * @return The catalog involved to resume its definition.
   */
  public <T> MachineCatalogDefinition builderPropertyConverter(
      Class<T> type,
      MachineBuilderPropertyConverter<T> converter) {
    converterSet.add(new MachineCatalogDefinitionBuilderPropertyConverter<>(type, converter));
    return this;
  }

  /**
   * Start defining the profile with the specified name.
   *
   * @param profileName Name of the profile to be defined.
   *
   * @return The builder to define the profile.
   */
  public ProfileBuilder profile(MachineProfileName profileName) {
    return new MachineCatalogProfileBuilderDefinition(this, profileName);
  }

  void putProfile(MachineProfileName profileName, MachineProfile profile) {
    profileMap.put(profileName, profile);
  }

  /**
   * Builder of a machine profile for a catalog.
   *
   * @see MachineCatalogDefinition#profile(MachineProfileName)
   */
  public interface ProfileBuilder {

    /**
     * Indicates that the supplied machine builder will be used for this profile.
     *
     * <p>
     * Example of builder usage,
     * 
     * <pre>
     * MachineCatalog catalog = new MachineCatalogDefinition()
     *     .profile(new MachineProfileName("some-profile"))
     *     .using(SomeMachineBuilder::new);
     * </pre>
     *
     * @param builderSupplier The supplier of the used machine builder.
     *
     * @return The catalog involved to resume its definition.
     */
    MachineCatalogDefinition using(Supplier<MachineBuilder> builderSupplier);

    /**
     * Start defining an <i>inline</i> machine builder for this profile.
     *
     * @param <P> Type used for machine builder properties.
     *
     * @param propertiesObjSupplier Supplier of the instance of the machine builder properties.
     *
     * @return The profile builder in <i>inline</i> mode.
     *
     * @see ProfileInlineBuilder#using(Function)
     */
    <P extends MachineBuilderPropertiesObject> ProfileInlineBuilder<P> inline(
        Supplier<P> propertiesObjSupplier);
  }

  /**
   * Builder of a machine profile, in <i>inline</i> mode, for a catalog.
   *
   * @param <P> Type used for machine builder properties.
   *
   * @see ProfileBuilder#inline(Supplier)
   */
  public interface ProfileInlineBuilder<P extends MachineBuilderPropertiesObject> {

    /**
     * Indicates the function used to define the machine builder in <i>inline</i> mode for this
     * profile.
     *
     * <p>
     * Example of using the builder in <i>inline</i> mode,
     * 
     * <pre>
     * MachineCatalog catalog = new MachineCatalogDefinition()
     *     .profile(new MachineProfileName("some-profile"))
     *     .inline(SomeMachineBuilderProperties::new)
     *     .using(builderProperties -&gt; builderContext -> ...);
     * </pre>
     *
     * @param inlineMapper Obtains an <i>inline</i> machine builder given the supplied properties.
     *
     * @return The catalog involved to resume its definition.
     */
    MachineCatalogDefinition using(Function<P, InlineMachineBuilder> inlineMapper);
  }

  /**
   * Adaptation of the machine builder interface for use in <i>inline</i> mode.
   *
   * <p>
   * This is a "functional" version of {@link MachineBuilder} whose properties are available in the
   * current scope thanks to {@link ProfileBuilder#inline(Supplier)}.
   *
   * @see ProfileInlineBuilder#using(Function)
   */
  @FunctionalInterface
  public interface InlineMachineBuilder {

    /**
     * Implementation of a machine build in <i>inline</i> mode.
     *
     * @param builderContext Build context available in scope.
     *
     * @return The result of building a machine.
     *
     * @throws MachineBuildingException In case of error during the build.
     * @throws InterruptedException If the thread in which the build is performed is interrupted.
     *
     * @see MachineBuilder#build(MachineBuilderContext)
     */
    MachineBuildingResult build(MachineBuilderContext builderContext)
        throws MachineBuildingException, InterruptedException;
  }
}
