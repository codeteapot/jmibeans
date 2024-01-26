package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.isEqual;

import com.github.codeteapot.jmibeans.examples.httpcert.catalog.MachineCommandError;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

class CertificatePutCommandExecution implements MachineShellCommandExecution<Void> {

  private final Certificate certificate;
  private final MachineCommandError commandError;

  CertificatePutCommandExecution(Charset charset, Certificate certificate) {
    this.certificate = requireNonNull(certificate);
    commandError = new MachineCommandError(charset);
  }

  @Override
  public void handleOutput(InputStream output) throws IOException {}

  @Override
  public void handleError(InputStream error) throws IOException {
    commandError.handleError(error);
  }

  @Override
  public void handleInput(OutputStream input) throws IOException, InterruptedException {
    try {
      input.write(certificate.getEncoded());
      input.close();
    } catch (CertificateEncodingException e) {
      throw new IOException(e);
    }
  }

  @Override
  public Void mapResult(int exitCode) throws Exception {
    commandError.throwExceptionIf(exitCode, isEqual(0).negate(), Exception::new);
    return null;
  }
}
