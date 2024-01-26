package com.github.codeteapot.jmibeans.depot.dns.catalog;

import static java.util.stream.Collectors.toSet;

import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import java.util.Set;

public class DNSHostFacet {

  private final Set<DNSManagedHost> hosts;

  public DNSHostFacet(MachineBuilderContext builderContext, Set<DNSHostName> hosts) {
    this.hosts = hosts.stream()
        .map(host -> new DNSManagedHost(builderContext, host))
        .collect(toSet());
  }

  public Set<? extends DNSHost> getHosts() {
    return hosts;
  }
}
