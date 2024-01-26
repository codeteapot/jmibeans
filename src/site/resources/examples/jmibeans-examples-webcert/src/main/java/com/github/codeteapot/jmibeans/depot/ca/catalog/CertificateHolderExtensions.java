package com.github.codeteapot.jmibeans.depot.ca.catalog;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.Optional;

public class CertificateHolderExtensions {

  private String configSection;
  private String subjectAltName;
  private String certificatePolicies;

  public CertificateHolderExtensions() {
    configSection = null;
    subjectAltName = null;
    certificatePolicies = null;
  }

  public CertificateHolderExtensions withConfigSection(String configSection) {
    this.configSection = requireNonNull(configSection);
    return this;
  }

  public CertificateHolderExtensions withSubjectAltName(String subjectAltName) {
    this.subjectAltName = requireNonNull(subjectAltName);
    return this;
  }

  public CertificateHolderExtensions withCertificatePolicies(String certificatePolicies) {
    this.certificatePolicies = requireNonNull(certificatePolicies);
    return this;
  }

  Optional<String> getConfigSection() {
    return ofNullable(configSection);
  }

  void appendToStatement(StringBuilder statement) {
    ofNullable(subjectAltName)
        .ifPresent(value -> statement.append(" -addext 'subjectAltName = ").append(value)
            .append("'"));
    ofNullable(certificatePolicies)
        .ifPresent(value -> statement.append(" -addext 'certificatePolicies = ").append(value)
            .append("'"));
  }
}
