package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.examples.csr.security.cert.CertificateSigningRequest;
import com.github.codeteapot.jmibeans.examples.csr.security.cert.CertificateSigningRequestException;
import com.github.codeteapot.jmibeans.shell.MachineShellCommand;

class GenerateKeyPairCommandFactory {

  private final String algorithm;
  private final int publicKeySize;

  GenerateKeyPairCommandFactory(String algorithm, int publicKeySize) {
    this.algorithm = requireNonNull(algorithm);
    this.publicKeySize = publicKeySize;
  }

  MachineShellCommand<CertificateSigningRequest> getCommand(
      String serverName,
      String privateKeyPath) throws CertificateSigningRequestException {
    switch (algorithm) {
      case GenerateRSAKeyPairCommand.ALGORITHM:
        return new GenerateRSAKeyPairCommand(serverName, publicKeySize, privateKeyPath);
      default:
        throw new CertificateSigningRequestException(
            format("Unsupported CSR algorithm %d", algorithm));
    }
  }
}
