package com.github.codeteapot.jmibeans.examples.csr.security.cert;

public class CertificateSigningRequestException extends Exception {

  private static final long serialVersionUID = 1L;

  public CertificateSigningRequestException(String message) {
    super(message);
  }

  public CertificateSigningRequestException(Throwable cause) {
    super(cause);
  }
}
