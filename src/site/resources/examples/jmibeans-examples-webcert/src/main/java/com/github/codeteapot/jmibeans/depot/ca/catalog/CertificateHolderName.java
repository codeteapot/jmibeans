package com.github.codeteapot.jmibeans.depot.ca.catalog;

import static java.util.Objects.requireNonNull;

public class CertificateHolderName {

  private final String value;

  public CertificateHolderName(String value) {
    this.value = requireNonNull(value);
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
    if (obj instanceof CertificateHolderName) {
      CertificateHolderName name = (CertificateHolderName) obj;
      return value.equals(name.value);
    }
    return false;
  }
}
