package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static java.util.function.Predicate.isEqual;

import com.github.codeteapot.jmibeans.examples.csr.security.cert.CertificateSigningRequest;
import com.github.codeteapot.jmibeans.examples.httpcert.catalog.MachineCommandError;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

class GenerateKeyPairCommandExecution
    implements MachineShellCommandExecution<CertificateSigningRequest> {

  private static final int BUFFER_SIZE = 256;

  private final MachineCommandError commandError;
  private byte[] encodedReq;

  GenerateKeyPairCommandExecution(Charset charset) {
    commandError = new MachineCommandError(charset);
    encodedReq = null;
  }

  @Override
  public void handleOutput(InputStream output) throws IOException {
    try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
      byte[] buf = new byte[BUFFER_SIZE];
      int len = output.read(buf, 0, BUFFER_SIZE);
      while (len > 0) {
        bytes.write(buf, 0, len);
        len = output.read(buf, 0, BUFFER_SIZE);
      }
      encodedReq = bytes.toByteArray();
    }
  }

  @Override
  public void handleError(InputStream error) throws IOException {
    commandError.handleError(error);
  }

  @Override
  public void handleInput(OutputStream input) throws IOException, InterruptedException {}

  @Override
  public CertificateSigningRequest mapResult(int exitCode) throws Exception {
    commandError.throwExceptionIf(exitCode, isEqual(0).negate(), Exception::new);
    return new WebServerCertificateSigningRequest(encodedReq);
  }
}
