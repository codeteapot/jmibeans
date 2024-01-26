package com.github.codeteapot.jmibeans.depot.ca.catalog;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public class CertificateHolderSubject {

  private final String commonName;
  private String organization;

  public CertificateHolderSubject(String commonName) {
    this.commonName = requireNonNull(commonName);
    organization = null;
  }

  public CertificateHolderSubject withOrganization(String organization) {
    this.organization = requireNonNull(organization);
    return this;
  }

  void appendToStatement(StringBuilder statement) {
    statement.append(" -subj '/CN=").append(commonName);
    ofNullable(organization).ifPresent(value -> statement.append(",/O=").append(value));
    statement.append("'");
  }
}
