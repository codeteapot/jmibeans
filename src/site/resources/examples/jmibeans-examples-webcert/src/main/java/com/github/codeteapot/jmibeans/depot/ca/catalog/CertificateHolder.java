package com.github.codeteapot.jmibeans.depot.ca.catalog;

import java.security.cert.Certificate;

public interface CertificateHolder {

  boolean isEmpty();

  void put(Certificate certificate);

  void clear();

  CertificateSigningRequest generateKeyPair() throws CertificateSigningRequestException;
}
