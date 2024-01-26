package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.nio.charset.Charset;
import java.security.cert.Certificate;

class CertificatePutCommand implements MachineShellCommand<Void> {

  private final String path;
  private final Certificate certificate;

  CertificatePutCommand(String path, Certificate certificate) {
    this.path = requireNonNull(path);
    this.certificate = requireNonNull(certificate);
  }

  @Override
  public String getStatement() {
    return new StringBuilder()
        .append("openssl x509 -outform PEM -out ").append(path)
        .toString();
  }

  @Override
  public MachineShellCommandExecution<Void> getExecution(Charset charset) {
    return new CertificatePutCommandExecution(charset, certificate);
  }
}
