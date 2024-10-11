package com.github.codeteapot.jmibeans.library.dns.catalog;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.Set;

public class DNSHostFacet {

  private final Set<DNSHost> hosts;

  DNSHostFacet(Set<DNSHost> hosts) {
    this.hosts = requireNonNull(hosts);
  }

  public Optional<DNSHost> getHost(String zoneName) {
    return hosts.stream()
        .filter(host -> zoneName.equals(host.getZoneName()))
        .findAny();
  }
}
