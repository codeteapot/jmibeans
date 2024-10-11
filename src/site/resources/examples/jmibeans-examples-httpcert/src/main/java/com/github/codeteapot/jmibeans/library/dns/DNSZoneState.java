package com.github.codeteapot.jmibeans.library.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHostFacet;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;

abstract class DNSZoneState {

  protected final DNSZoneStateChanger stateChanger;
  protected final PlatformContext context;
  protected final String name;

  protected DNSZoneState(
      DNSZoneStateChanger stateChanger,
      PlatformContext context,
      String name) {
    this.stateChanger = requireNonNull(stateChanger);
    this.context = requireNonNull(context);
    this.name = requireNonNull(name);
  }

  abstract void available(MachineRef serverRef, DNSServerFacet serverFacet);

  abstract void available(MachineRef hostRef, DNSHostFacet hostFacet);

  abstract void lost(MachineRef machineRef);
}
