package com.github.codeteapot.jmibeans.library.dns;

import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHost;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHostFacet;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import java.util.HashSet;
import java.util.Set;

class DomainZoneUnavailableState extends DomainZoneState {

  private final Set<DomainHost> pendingHosts;

  DomainZoneUnavailableState(
      DomainZoneStateChanger stateChanger,
      PlatformContext context,
      String name) {
    super(stateChanger, context, name);
    pendingHosts = new HashSet<>();
  }

  @Override
  void available(MachineRef serverRef, DNSServerFacet serverFacet) {
    serverFacet.createZone(name);
    pendingHosts.forEach(host -> {
      context.lookup(host.getMachineRef())
      .
    });
    stateChanger.available(context, name, serverRef);
  }

  @Override
  void available(MachineRef hostRef, DNSHost host) {
    pendingHosts.add(new DomainHost(hostRef, host));
  }

  @Override
  void lost(MachineRef machineRef) {
    pendingHosts.removeIf(host -> machineRef.equals(host.getMachineRef()));
  }
}
