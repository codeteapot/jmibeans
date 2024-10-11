package com.github.codeteapot.jmibeans.library.dns;

import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHost;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import java.util.function.Consumer;

class DomainZone {

  private DomainZoneState state;

  DomainZone(PlatformContext context, String name) {
    state = initialState(newState -> state = newState, context, name);
  }
  
  String getName() {
    return state.name;
  }

  void available(MachineRef serverRef, DNSServerFacet serverFacet) {
    state.available(serverRef, serverFacet);
  }

  void available(MachineRef hostRef, DNSHost host) {
    state.available(hostRef, host);
  }

  void lost(MachineRef machineRef) {
    state.lost(machineRef);
  }

  private static DomainZoneState initialState(
      Consumer<DomainZoneState> changeStateAction,
      PlatformContext context,
      String name) {
    return new DomainZoneUnavailableState(
        new DomainZoneStateChanger(changeStateAction),
        context,
        name);
  }
}
