package com.github.codeteapot.jmibeans.depot.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSZoneName;

public class DNSLayoutZone {

  private final DNSZoneName name;

  public DNSLayoutZone(DNSZoneName name) {
    this.name = requireNonNull(name);
  }

  public DNSZoneName getName() {
    return name;
  }
}
