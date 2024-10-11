package com.github.codeteapot.jmibeans.library.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHostFacet;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSServerFacet;
import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.platform.event.PlatformListener;

public class DNSResolver implements PlatformListener {

  private final PlatformContext context;
  private final DomainZone zone;

  public DNSResolver(PlatformContext context, String zoneName) {
    this.context = requireNonNull(context);
    zone = new DomainZone(context, zoneName);
  }

  @Override
  public void machineAvailable(MachineAvailableEvent event) {
    context.lookup(event.getMachineRef())
        .flatMap(Machine.facetGet(DNSServerFacet.class))
        .ifPresent(serverFacet -> zone.available(event.getMachineRef(), serverFacet));
    context.lookup(event.getMachineRef())
        .flatMap(Machine.facetGet(DNSHostFacet.class))
        .flatMap(hostFacet -> hostFacet.getHost(zone.getName()))
        .ifPresent(host -> zone.available(event.getMachineRef(), host));
  }

  @Override
  public void machineLost(MachineLostEvent event) {
    zone.lost(event.getMachineRef());
  }
}
