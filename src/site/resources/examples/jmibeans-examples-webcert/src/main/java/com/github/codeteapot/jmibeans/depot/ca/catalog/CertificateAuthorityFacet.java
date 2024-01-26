package com.github.codeteapot.jmibeans.depot.ca.catalog;

import static com.github.codeteapot.jmibeans.shell.MachineShellCommand.shellCommand;
import static com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory.DEFAULT_USERNAME;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .ignoreOutput;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .readOutputString;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .returnNull;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .statelessShellCommandExecution;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .throwExceptionIf;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .withoutInput;
import static java.lang.String.format;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static java.util.function.Predicate.isEqual;

import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.UUID;

public class CertificateAuthorityFacet {

  private static final String CERTIFICATE_FACTORY_TYPE = "X.509";

  private static final String CSR_FILE_PATH_PATTERN = "%s/csr/%s.csr";
  private static final String NEW_CERTS_FILE_PATH_PATTERN = "%s/newcerts/%s.pem";

  private final MachineShellConnectionFactory connectionFactory;
  private final CertificateFactory certificateFactory;
  private final String baseDir;

  public CertificateAuthorityFacet(
      MachineShellConnectionFactory connectionFactory,
      String baseDir) throws MachineBuildingException {
    try {
      this.connectionFactory = requireNonNull(connectionFactory);
      certificateFactory = CertificateFactory.getInstance(CERTIFICATE_FACTORY_TYPE);
      this.baseDir = requireNonNull(baseDir);
    } catch (CertificateException e) {
      throw new MachineBuildingException(e);
    }
  }

  public Certificate issue(CertificateSigningRequest csr) throws CertificateAuthorityException {
    try (
        MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME);
        FileSystem fs = connection.getFileSystem()) {
      UUID certificateId = randomUUID();
      try (OutputStream csrOutput = newOutputStream(fs.getPath(format(
          CSR_FILE_PATH_PATTERN,
          baseDir,
          certificateId)))) {
        csrOutput.write(csr.getEncoded());
      }
      StringBuilder statement = new StringBuilder()
          .append("openssl ca")
          .append(" -config ").append(baseDir).append("/signer.cnf")
          .append(" -days 375")
          .append(" -notext")
          .append(" -md sha256")
          .append(" -in ").append(baseDir).append("/csr/").append(certificateId).append(".csr")
          .append(" -inform DER ")
          .append(" -out ").append(baseDir).append("/newcerts/").append(certificateId)
          .append(".pem")
          .append(" -passin pass:12345678")
          .append(" -batch");
      csr.getExtensionsConfigSection().ifPresent(value -> statement.append(" -extensions ")
          .append(value));
      connection.execute(shellCommand(
          statement.toString(),
          statelessShellCommandExecution(
              ignoreOutput(),
              readOutputString().map(Exception::new),
              withoutInput(),
              throwExceptionIf(isEqual(0).negate(), returnNull()))));
      try (InputStream crtInput = newInputStream(fs.getPath(format(
          NEW_CERTS_FILE_PATH_PATTERN,
          baseDir,
          certificateId)))) {
        return certificateFactory.generateCertificate(crtInput);
      }
    } catch (MachineShellException | CertificateException | IOException e) {
      throw new CertificateAuthorityException(e);
    }
  }
}
