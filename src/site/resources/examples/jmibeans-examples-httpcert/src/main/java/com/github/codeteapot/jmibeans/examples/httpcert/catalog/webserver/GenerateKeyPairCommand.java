package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import com.github.codeteapot.jmibeans.examples.csr.security.cert.CertificateSigningRequest;
import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.nio.charset.Charset;

abstract class GenerateKeyPairCommand implements MachineShellCommand<CertificateSigningRequest> {

  GenerateKeyPairCommand() {}

  @Override
  public MachineShellCommandExecution<CertificateSigningRequest> getExecution(Charset charset) {
    return new GenerateKeyPairCommandExecution(charset);
  }
}
