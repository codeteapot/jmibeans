package com.github.codeteapot.jmibeans.depot.ca.catalog;

import static java.util.Optional.ofNullable;

import java.io.Serializable;
import java.util.Optional;

public class CertificateSigningRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private final byte[] encodedReq;
  private final String extensionsConfigSection;

  public CertificateSigningRequest(byte[] encodedReq, String extensionsConfigSection) {
    this.encodedReq = encodedReq.clone();
    this.extensionsConfigSection = extensionsConfigSection;
  }

  public byte[] getEncoded() {
    return encodedReq.clone();
  }

  public Optional<String> getExtensionsConfigSection() {
    return ofNullable(extensionsConfigSection);
  }
}
