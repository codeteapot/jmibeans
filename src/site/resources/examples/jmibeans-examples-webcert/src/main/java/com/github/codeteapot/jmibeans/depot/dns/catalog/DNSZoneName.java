package com.github.codeteapot.jmibeans.depot.dns.catalog;

import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class DNSZoneName implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String value;

  @ConstructorProperties("value")
  public DNSZoneName(String value) {
    this.value = requireNonNull(value);
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
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
    if (obj instanceof DNSZoneName) {
      DNSZoneName zoneName = (DNSZoneName) obj;
      return value.equals(zoneName.value);
    }
    return false;
  }
}
