package com.github.codeteapot.jmibeans.library.dns;

import com.github.codeteapot.jmibeans.library.dns.catalog.DomainNameServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import java.util.LinkedList;
import java.util.Queue;

class UnavailableDomainZone implements DomainZone {

  private final Queue<String> pendingDomains;
  
  UnavailableDomainZone() {
    pendingDomains = new LinkedList<>();
  }

  @Override
  public DomainZone available(MachineRef serverRef, DomainNameServerFacet serverFacet) {
    throw new UnsupportedOperationException();
  }
}
