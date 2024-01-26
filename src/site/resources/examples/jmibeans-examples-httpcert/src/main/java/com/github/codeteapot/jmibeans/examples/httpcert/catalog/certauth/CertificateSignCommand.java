package com.github.codeteapot.jmibeans.examples.httpcert.catalog.certauth;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.nio.charset.Charset;
import java.util.UUID;

class CertificateSignCommand implements MachineShellCommand<Void> {

  private final String baseDir;
  private final UUID certificateId;

  CertificateSignCommand(String baseDir, UUID certificateId) {
    this.baseDir = requireNonNull(baseDir);
    this.certificateId = requireNonNull(certificateId);
  }

  @Override
  public String getStatement() {
    return new StringBuilder()
        .append("openssl ca ")
        .append("-config ").append(baseDir).append("/signer.cnf ")
        .append("-extensions server_cert ")
        .append("-days 375 ")
        .append("-notext ")
        .append("-md sha256 ")
        .append("-in ").append(baseDir).append("/csr/").append(certificateId).append(".csr ")
        .append("-inform DER ")
        .append("-out ").append(baseDir).append("/newcerts/").append(certificateId).append(".pem ")
        .append("-passin pass:12345678 ")
        .append("-batch ")
        .toString();
  }

  @Override
  public MachineShellCommandExecution<Void> getExecution(Charset charset) {
    return new CertificateSignCommandExecution(charset);
  }
}
