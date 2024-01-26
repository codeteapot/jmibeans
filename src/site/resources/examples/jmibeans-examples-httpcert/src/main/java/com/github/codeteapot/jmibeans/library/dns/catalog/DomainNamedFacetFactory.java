package com.github.codeteapot.jmibeans.library.dns.catalog;

import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import java.util.function.Supplier;

public class DomainNamedFacetFactory {

  public DomainNamedFacet getFacet(
      MachineBuilderContext builderContext,
      MachineNetworkName networkName,
      Supplier<String> domainNameSupplier) {
    return new DomainNamedFacet(builderContext, networkName, domainNameSupplier);
  }
}
