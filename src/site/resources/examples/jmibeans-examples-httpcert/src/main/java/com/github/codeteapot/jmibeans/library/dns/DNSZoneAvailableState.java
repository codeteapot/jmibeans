package com.github.codeteapot.jmibeans.library.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHostFacet;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import java.util.HashSet;
import java.util.Set;

class DNSZoneAvailableState extends DNSZoneState {

  private final MachineRef serverRef;
  private final Set<DNSReferencedHost> registeredHosts;

  DNSZoneAvailableState(
      DNSZoneStateChanger stateChanger,
      PlatformContext context,
      String name,
      MachineRef serverRef) {
    super(stateChanger, context, name);
    this.serverRef = requireNonNull(serverRef);
    registeredHosts = new HashSet<>();
  }

  @Override
  void available(MachineRef serverRef, DNSServerFacet serverFacet) {
    throw new IllegalStateException("Already available");
  }

  @Override
  void available(MachineRef hostRef, DNSHostFacet hostFacet) {
    // TODO Auto-generated method stub

  }

  @Override
  void lost(MachineRef machineRef) {
    if (serverRef.equals(machineRef)) {
      stateChanger.unavailable(context, name);
    } else {
      registeredHosts.stream()
          .filter(host -> machineRef.equals(host.getMachineRef()))
          .findAny()
          .ifPresent(this::hostLost);
    }
  }

  private void hostLost(DNSReferencedHost host) {
    // TODO ...
  }

  @Deprecated
  private boolean hasServerRef(MachineRef machineRef) {
    return serverRef.equals(machineRef);
  }

  @Deprecated
  private boolean hasNotRegisteredHostRef(MachineRef machineRef) {
    return registeredHosts.stream()
        .map(DNSReferencedHost::getMachineRef)
        .noneMatch(machineRef::equals);
  }
}
