package com.github.codeteapot.jmibeans.library.dns;

import com.github.codeteapot.jmibeans.library.dns.catalog.DomainNameServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import java.util.HashSet;
import java.util.Set;

class AvailableDomainZone implements DomainZone {

  private final Set<RegisteredDomain> registeredDomains;

  AvailableDomainZone() {
    registeredDomains = new HashSet<>();
  }

  @Override
  public DomainZone available(MachineRef serverRef, DomainNameServerFacet serverFacet) {
    throw new UnsupportedOperationException();
  }
}
