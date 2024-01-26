package com.github.codeteapot.jmibeans.depot.ca.catalog;

import static com.github.codeteapot.jmibeans.shell.MachineShellCommand.shellCommand;
import static com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory.DEFAULT_USERNAME;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .closeInput;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .ignoreOutput;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .readOutputByteArray;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .readOutputString;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .returnNull;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .returnOutput;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .statelessShellCommandExecution;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .throwExceptionIf;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .withoutInput;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution //
    .writeInput;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.isEqual;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.security.cert.Certificate;

class FileSystemCertificateHolder implements CertificateHolder {

  private final CertificateHolderName name;
  private final MachineShellConnectionFactory connectionFactory;
  private final CertificateHolderSubject subject;
  private final String privateKeyAlgorithm;
  private final int privateKeySize;
  private final boolean nodes;
  private final CertificateHolderExtensions extensions;
  private final String certificatePath;
  private final String privateKeyPath;

  FileSystemCertificateHolder(
      CertificateHolderName name,
      MachineShellConnectionFactory connectionFactory,
      CertificateHolderSubject subject,
      String privateKeyAlgorithm,
      int privateKeySize,
      boolean nodes,
      CertificateHolderExtensions extensions,
      String certificatePath,
      String privateKeyPath) {
    this.name = requireNonNull(name);
    this.connectionFactory = requireNonNull(connectionFactory);
    this.subject = requireNonNull(subject);
    this.privateKeyAlgorithm = requireNonNull(privateKeyAlgorithm);
    this.privateKeySize = privateKeySize;
    this.nodes = nodes;
    this.extensions = requireNonNull(extensions);
    this.certificatePath = requireNonNull(certificatePath);
    this.privateKeyPath = requireNonNull(privateKeyPath);
  }

  @Override
  public boolean isEmpty() {
    try (
        MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME);
        FileSystem fs = connection.getFileSystem()) {
      return !exists(fs.getPath(certificatePath));
    } catch (MachineShellException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void put(Certificate certificate) {
    try (MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME)) {
      connection.execute(shellCommand(
          new StringBuilder()
              .append("openssl x509 -outform PEM -out ").append(certificatePath)
              .toString(),
          statelessShellCommandExecution(
              ignoreOutput(),
              readOutputString().map(Exception::new),
              writeInput(certificate::getEncoded).andThen(closeInput()),
              throwExceptionIf(isEqual(0).negate(), returnNull()))));
    } catch (MachineShellException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void clear() {
    try (
        MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME);
        FileSystem fs = connection.getFileSystem()) {
      delete(fs.getPath(certificatePath));
      delete(fs.getPath(privateKeyPath));
    } catch (MachineShellException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public CertificateSigningRequest generateKeyPair() throws CertificateSigningRequestException {
    try (MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME)) {
      StringBuilder statement = new StringBuilder()
          .append("openssl req")
          .append(" -new")
          .append(" -newkey ").append(privateKeyAlgorithm.toLowerCase()).append(':')
          .append(privateKeySize)
          .append(" -sha256")
          .append(" -keyout ").append(privateKeyPath)
          .append(" -outform DER");
      if (nodes) {
        statement.append(" -nodes");
      }
      subject.appendToStatement(statement);
      extensions.appendToStatement(statement);
      return connection.execute(shellCommand(
          statement.toString(),
          statelessShellCommandExecution(
              readOutputByteArray().map(this::newSigningRequest),
              readOutputString().map(Exception::new),
              withoutInput(),
              throwExceptionIf(isEqual(0).negate(), returnOutput()))));
    } catch (MachineShellException e) {
      throw new CertificateSigningRequestException(e);
    }
  }

  boolean match(CertificateHolderName name) {
    return name.equals(this.name);
  }

  private CertificateSigningRequest newSigningRequest(byte[] encodedReq) {
    return new CertificateSigningRequest(encodedReq, extensions.getConfigSection().orElse(null));
  }
}
