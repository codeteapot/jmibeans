package com.github.codeteapot.jmibeans.depot.dns.catalog;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineNetworkName;

public class DNSZoneSettings {

  private final DNSZoneName name;
  private final MachineNetworkName networkName;

  public DNSZoneSettings(DNSZoneName name, MachineNetworkName networkName) {
    this.name = requireNonNull(name);
    this.networkName = requireNonNull(networkName);
  }

  DNSZoneName getName() {
    return name;
  }

  MachineNetworkName getNetworkName() {
    return networkName;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof DNSZoneSettings) {
      DNSZoneSettings settings = (DNSZoneSettings) obj;
      return name.equals(settings.name);
    }
    return false;
  }
}
