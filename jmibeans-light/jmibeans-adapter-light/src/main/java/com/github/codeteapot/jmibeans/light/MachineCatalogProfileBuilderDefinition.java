package com.github.codeteapot.jmibeans.light;

import static java.util.Objects.requireNonNull;
import com.github.codeteapot.jmibeans.light.MachineCatalogDefinition.ProfileInlineBuilder;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertiesObject;
import java.util.function.Supplier;

class MachineCatalogProfileBuilderDefinition implements MachineCatalogDefinition.ProfileBuilder {

  private final MachineCatalogDefinition parent;
  private final MachineProfileName name;

  MachineCatalogProfileBuilderDefinition(MachineCatalogDefinition parent, MachineProfileName name) {
    this.parent = requireNonNull(parent);
    this.name = requireNonNull(name);
  }

  @Override
  public MachineCatalogDefinition using(Supplier<MachineBuilder> builderSupplier) {
    parent.putProfile(name, new MachineCatalogProfileDefinition(builderSupplier));
    return parent;
  }

  @Override
  public <P extends MachineBuilderPropertiesObject> ProfileInlineBuilder<P> inline(
      Supplier<P> propertiesObjSupplier) {
    return new MachineCatalogProfileInlineBuilderDefinition<>(parent, name, propertiesObjSupplier);
  }
}
