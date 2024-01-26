package com.github.codeteapot.jmibeans.depot.dns;

import static java.util.Objects.requireNonNull;

import java.util.Set;

public class DNSLayout {

  private Set<DNSLayoutZone> zones;

  public DNSLayout(Set<DNSLayoutZone> zones) {
    this.zones = requireNonNull(zones);
  }

  public Set<DNSLayoutZone> getZones() {
    return zones;
  }
}
