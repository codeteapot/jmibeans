package com.github.codeteapot.jmibeans.library.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHost;
import com.github.codeteapot.jmibeans.platform.MachineRef;

class DNSReferencedHost {

  private final MachineRef hostRef;
  private final DNSHost host;

  DNSReferencedHost(MachineRef hostRef, DNSHost host) {
    this.hostRef = requireNonNull(hostRef);
    this.host = requireNonNull(host);
  }

  MachineRef getHostRef() {
    return hostRef;
  }
}
