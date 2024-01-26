package com.github.codeteapot.jmibeans.depot.ca.catalog;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;

public class CertificateHolderSettings {

  public static final String DEFAULT_SUBJECT_COMMON_NAME = "localhost";
  public static final String DEFAULT_PRIVATE_KEY_ALGORITHM = "RSA";
  public static final int DEFAULT_PRIVATE_KEY_SIZE = 1024;
  public static final boolean DEFAULT_NODES = false;
  public static final String DEFAULT_CERTIFICATE_PATH = "/etc/ssl/cert.pem";
  public static final String DEFAULT_PRIVATE_KEY_PATH = "/etc/ssl/private/key.pem";

  private final CertificateHolderName name;
  private CertificateHolderSubject subject;
  private String privateKeyAlgorithm;
  private int privateKeySize;
  private boolean nodes;
  private CertificateHolderExtensions extensions;
  private String certificatePath;
  private String privateKeyPath;

  public CertificateHolderSettings(CertificateHolderName name) {
    this.name = requireNonNull(name);
    subject = new CertificateHolderSubject(DEFAULT_SUBJECT_COMMON_NAME);
    privateKeyAlgorithm = DEFAULT_PRIVATE_KEY_ALGORITHM;
    privateKeySize = DEFAULT_PRIVATE_KEY_SIZE;
    nodes = DEFAULT_NODES;
    extensions = new CertificateHolderExtensions();
    certificatePath = DEFAULT_CERTIFICATE_PATH;
    privateKeyPath = DEFAULT_PRIVATE_KEY_PATH;
  }

  public CertificateHolderSettings withSubject(CertificateHolderSubject subject) {
    this.subject = requireNonNull(subject);
    return this;
  }

  public CertificateHolderSettings withPrivateKeyAlgorithm(String privateKeyAlgorithm) {
    this.privateKeyAlgorithm = requireNonNull(privateKeyAlgorithm);
    return this;
  }

  public CertificateHolderSettings withPrivateKeySize(int privateKeySize) {
    this.privateKeySize = privateKeySize;
    return this;
  }

  public CertificateHolderSettings withNodes(boolean nodes) {
    this.nodes = nodes;
    return this;
  }

  public CertificateHolderSettings withExtensions(CertificateHolderExtensions extensions) {
    this.extensions = requireNonNull(extensions);
    return this;
  }

  public CertificateHolderSettings withCertificatePath(String certificatePath) {
    this.certificatePath = requireNonNull(certificatePath);
    return this;
  }

  public CertificateHolderSettings withPrivateKeyPath(String privateKeyPath) {
    this.privateKeyPath = requireNonNull(privateKeyPath);
    return this;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof CertificateHolderSettings) {
      CertificateHolderSettings holder = (CertificateHolderSettings) obj;
      return name.equals(holder.name);
    }
    return false;
  }

  FileSystemCertificateHolder create(MachineShellConnectionFactory connectionFactory) {
    return new FileSystemCertificateHolder(
        name,
        connectionFactory,
        subject,
        privateKeyAlgorithm,
        privateKeySize,
        nodes,
        extensions,
        certificatePath,
        privateKeyPath);
  }
}
