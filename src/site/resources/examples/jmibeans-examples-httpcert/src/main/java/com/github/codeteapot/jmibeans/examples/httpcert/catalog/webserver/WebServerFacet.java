package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver //
    .WebServerFacetFactory.HTTP_ADMIN_USER;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.examples.csr.security.cert.CertificateSigningRequest;
import com.github.codeteapot.jmibeans.examples.csr.security.cert.CertificateSigningRequestException;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.security.cert.Certificate;

public class WebServerFacet {

  private final MachineShellConnectionFactory connectionFactory;
  private final GenerateKeyPairCommandFactory generateKeyPairCommandFactory;
  private final String serverName;
  private final String siteName;
  private final String certificatePath;
  private final String privateKeyPath;

  WebServerFacet(
      MachineShellConnectionFactory connectionFactory,
      GenerateKeyPairCommandFactory generateKeyPairCommandFactory,
      String serverName,
      String siteName,
      String certificatePath,
      String privateKeyPath) {
    this.connectionFactory = requireNonNull(connectionFactory);
    this.generateKeyPairCommandFactory = requireNonNull(generateKeyPairCommandFactory);
    this.serverName = requireNonNull(serverName);
    this.siteName = requireNonNull(siteName);
    this.certificatePath = requireNonNull(certificatePath);
    this.privateKeyPath = requireNonNull(privateKeyPath);
  }

  public boolean isCertificateEmpty() {
    try (MachineShellConnection connection = connectionFactory.getConnection(HTTP_ADMIN_USER)) {
      return !connection.execute(new FileExistsCommand(certificatePath));
    } catch (MachineShellException | MachineShellCommandExecutionException e) {
      throw new IllegalStateException(e);
    }
  }

  public void certificatePut(Certificate certificate) {
    try (MachineShellConnection connection = connectionFactory.getConnection(HTTP_ADMIN_USER)) {
      connection.execute(new CertificatePutCommand(certificatePath, certificate));
      connection.execute(new EnableSiteCommand(siteName));
    } catch (MachineShellException | MachineShellCommandExecutionException e) {
      throw new IllegalStateException(e);
    }
  }

  public void certificateClear() {
    try (MachineShellConnection connection = connectionFactory.getConnection(HTTP_ADMIN_USER)) {
      connection.execute(new CertificateRemoveCommand(certificatePath, privateKeyPath));
      connection.execute(new DisableSiteCommand(siteName));
    } catch (MachineShellException | MachineShellCommandExecutionException e) {
      throw new IllegalStateException(e);
    }
  }

  public CertificateSigningRequest generateKeyPair()
      throws CertificateSigningRequestException {
    try (MachineShellConnection connection = connectionFactory.getConnection(HTTP_ADMIN_USER)) {
      return connection.execute(generateKeyPairCommandFactory.getCommand(
          serverName,
          privateKeyPath));
    } catch (MachineShellException | MachineShellCommandExecutionException e) {
      throw new CertificateSigningRequestException(e);
    }
  }
}
