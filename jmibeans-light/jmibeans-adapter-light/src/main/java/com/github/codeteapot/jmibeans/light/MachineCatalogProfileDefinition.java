package com.github.codeteapot.jmibeans.light;

import static java.util.Objects.requireNonNull;
import java.util.function.Supplier;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineProfile;

class MachineCatalogProfileDefinition implements MachineProfile {

  private final Supplier<MachineBuilder> builderSupplier;

  MachineCatalogProfileDefinition(Supplier<MachineBuilder> builderSupplier) {
    this.builderSupplier = requireNonNull(builderSupplier);
  }

  @Override
  public MachineBuilder getBuilder() {
    return builderSupplier.get();
  }
}
