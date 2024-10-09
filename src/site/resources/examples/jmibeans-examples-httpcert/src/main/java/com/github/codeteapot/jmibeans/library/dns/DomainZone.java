package com.github.codeteapot.jmibeans.library.dns;

import com.github.codeteapot.jmibeans.library.dns.catalog.DomainNameServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;

interface DomainZone {
  
  DomainZone available(MachineRef serverRef, DomainNameServerFacet serverFacet);
}
