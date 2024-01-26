package com.github.codeteapot.jmibeans.examples.csr.security.cert;

import java.io.Serializable;

public abstract class CertificateSigningRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  public abstract byte[] getEncoded();
}
