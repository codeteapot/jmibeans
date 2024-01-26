package com.github.codeteapot.jmibeans.examples.httpcert.catalog.certauth;

import static com.github.codeteapot.jmibeans.examples.httpcert.catalog.certauth //
    .CertificateAuthorityFacetFactory.CERT_ISSUER_USER;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

import com.github.codeteapot.jmibeans.examples.csr.security.cert.CertificateSigningRequest;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.UUID;

public class CertificateAuthorityFacet {

  private static final String CSR_FILE_PATH_PATTERN = "%s/csr/%s.csr";
  private static final String NEW_CERTS_FILE_PATH_PATTERN = "%s/newcerts/%s.pem";

  private final MachineShellConnectionFactory connectionFactory;
  private final CertificateFactory certificateFactory;
  private final String baseDir;

  CertificateAuthorityFacet(
      MachineShellConnectionFactory connectionFactory,
      CertificateFactory certificateFactory,
      String baseDir) {
    this.connectionFactory = requireNonNull(connectionFactory);
    this.certificateFactory = requireNonNull(certificateFactory);
    this.baseDir = requireNonNull(baseDir);
  }

  public Certificate issue(CertificateSigningRequest csr) throws CertificateAuthorityException {
    try (MachineShellConnection connection = connectionFactory.getConnection(CERT_ISSUER_USER)) {
      UUID certificateId = randomUUID();
      try (OutputStream csrOutput = connection.file(format(
          CSR_FILE_PATH_PATTERN,
          baseDir,
          certificateId)).getOutputStream()) {
        csrOutput.write(csr.getEncoded());
      }
      connection.execute(new CertificateSignCommand(baseDir, certificateId));
      try (InputStream crtInput = connection.file(format(
          NEW_CERTS_FILE_PATH_PATTERN,
          baseDir,
          certificateId)).getInputStream()) {
        return certificateFactory.generateCertificate(crtInput);
      }
    } catch (MachineShellException
        | MachineShellCommandExecutionException
        | CertificateException
        | IOException e) {
      throw new CertificateAuthorityException(e);
    }
  }
}
