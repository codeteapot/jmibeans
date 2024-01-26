package com.github.codeteapot.jmibeans.depot.dns.catalog;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class DNSHostName {

  private final DNSZoneName zoneName;
  private final String value;

  public DNSHostName(DNSZoneName zoneName, String value) {
    this.zoneName = requireNonNull(zoneName);
    this.value = requireNonNull(value);
  }

  public DNSZoneName getZoneName() {
    return zoneName;
  }

  public String getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof DNSHostName) {
      DNSHostName hostName = (DNSHostName) obj;
      return zoneName.equals(hostName.zoneName) && value.equals(hostName.value);
    }
    return false;
  }

  @Override
  public String toString() {
    return format("%s.%s", value, zoneName);
  }
}
