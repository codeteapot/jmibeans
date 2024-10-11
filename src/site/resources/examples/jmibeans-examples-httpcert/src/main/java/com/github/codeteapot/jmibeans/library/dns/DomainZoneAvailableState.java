package com.github.codeteapot.jmibeans.library.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHost;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import java.util.HashSet;
import java.util.Set;

class DomainZoneAvailableState extends DomainZoneState {

  private final MachineRef serverRef;
  private final Set<DomainHost> registeredHosts;

  DomainZoneAvailableState(
      DomainZoneStateChanger stateChanger,
      PlatformContext context,
      String name,
      MachineRef serverRef) {
    super(stateChanger, context, name);
    this.serverRef = requireNonNull(serverRef);
    registeredHosts = new HashSet<>();
  }

  @Deprecated
  private boolean hasServerRef(MachineRef machineRef) {
    return serverRef.equals(machineRef);
  }

  @Deprecated
  private boolean hasNotRegisteredHostRef(MachineRef machineRef) {
    return registeredHosts.stream()
        .map(DomainHost::getMachineRef)
        .noneMatch(machineRef::equals);
  }

  @Override
  public void available(MachineRef serverRef, DNSServerFacet serverFacet) {
    throw new IllegalStateException("Already available");
  }

  @Override
  void available(MachineRef hostRef, DNSHost host) {
    // TODO Auto-generated method stub

  }

  @Override
  public void lost(MachineRef machineRef) {
    if (serverRef.equals(machineRef)) {
      stateChanger.unavailable(context, name);
    } else {
      registeredHosts.stream()
          .filter(host -> machineRef.equals(host.getMachineRef()))
          .findAny()
          .ifPresent(this::hostLost);
    }
  }

  private void hostLost(DomainHost host) {
    // TODO ...
  }
}
