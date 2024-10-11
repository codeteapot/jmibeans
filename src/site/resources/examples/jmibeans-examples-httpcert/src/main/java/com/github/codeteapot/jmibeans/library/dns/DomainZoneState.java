package com.github.codeteapot.jmibeans.library.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHost;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;

abstract class DomainZoneState {

  protected final DomainZoneStateChanger stateChanger;
  protected final PlatformContext context;
  protected final String name;

  protected DomainZoneState(
      DomainZoneStateChanger stateChanger,
      PlatformContext context,
      String name) {
    this.stateChanger = requireNonNull(stateChanger);
    this.context = requireNonNull(context);
    this.name = requireNonNull(name);
  }

  abstract void available(MachineRef serverRef, DNSServerFacet serverFacet);

  abstract void available(MachineRef hostRef, DNSHost host);

  abstract void lost(MachineRef machineRef);
}
