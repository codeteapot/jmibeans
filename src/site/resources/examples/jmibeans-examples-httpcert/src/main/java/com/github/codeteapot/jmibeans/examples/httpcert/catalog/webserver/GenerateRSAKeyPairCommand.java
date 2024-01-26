package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static java.util.Objects.requireNonNull;

class GenerateRSAKeyPairCommand extends GenerateKeyPairCommand {

  static final String ALGORITHM = "RSA";

  private final String serverName;
  private final int size;
  private final String privateKeyPath;

  GenerateRSAKeyPairCommand(String serverName, int size, String privateKeyPath) {
    this.size = size;
    this.privateKeyPath = requireNonNull(privateKeyPath);
    this.serverName = requireNonNull(serverName);
  }

  @Override
  public String getStatement() {
    return new StringBuilder()
        .append("openssl req ")
        .append("-new ")
        .append("-newkey rsa:").append(size).append(" ")
        .append("-sha256 ")
        .append("-nodes ")
        .append("-keyout ").append(privateKeyPath).append(" ")
        .append("-subj '")
        .append("/CN=").append(serverName)
        .append("' ")
        .append("-outform DER ")
        .append("-addext 'subjectAltName = DNS:").append(serverName).append("' ")
        .append("-addext 'certificatePolicies = 1.2.3.4'")
        .toString();
  }
}
