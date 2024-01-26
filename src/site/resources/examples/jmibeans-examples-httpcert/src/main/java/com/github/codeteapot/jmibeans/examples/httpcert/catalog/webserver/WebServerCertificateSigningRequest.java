package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import com.github.codeteapot.jmibeans.examples.csr.security.cert.CertificateSigningRequest;

class WebServerCertificateSigningRequest extends CertificateSigningRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final byte[] encodedReq;
  
  WebServerCertificateSigningRequest(byte[] encodedReq) {
    this.encodedReq = encodedReq.clone();
  }

  @Override
  public byte[] getEncoded() {
    return encodedReq.clone();
  }
}
