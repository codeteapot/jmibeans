package com.github.codeteapot.jmibeans.light;

import static java.util.Objects.requireNonNull;
import com.github.codeteapot.jmibeans.light.MachineCatalogDefinition.InlineMachineBuilder;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertiesObject;
import java.util.function.Function;
import java.util.function.Supplier;

class MachineCatalogProfileInlineBuilderDefinition<P extends MachineBuilderPropertiesObject>
    implements MachineCatalogDefinition.ProfileInlineBuilder<P> {

  private final MachineCatalogDefinition parent;
  private final MachineProfileName name;
  private final Supplier<P> propertiesObjSupplier;

  MachineCatalogProfileInlineBuilderDefinition(
      MachineCatalogDefinition parent,
      MachineProfileName name,
      Supplier<P> propertiesObjSupplier) {
    this.parent = requireNonNull(parent);
    this.name = requireNonNull(name);
    this.propertiesObjSupplier = requireNonNull(propertiesObjSupplier);
  }

  @Override
  public MachineCatalogDefinition using(Function<P, InlineMachineBuilder> inlineMapper) {
    parent.putProfile(
        name,
        new MachineCatalogProfileDefinition(() -> new InlineMachineBuilderWrapper<>(
            propertiesObjSupplier.get(),
            inlineMapper)));
    return parent;
  }
}
