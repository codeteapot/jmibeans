package com.github.codeteapot.jmibeans.session;

import static java.util.Objects.requireNonNull;

import com.jcraft.jsch.JSch;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

/**
 * Reference implementation of a {@link MachineSessionFactory}.
 *
 * <p>It incorporates operations to create the credentials that will be necessary to successfully
 * carry out the authentication processes when attempting to establish a session.
 */
public class MachineSessionClient implements MachineSessionFactory {

  private static final long EXECUTION_TIMEOUT_MILLIS = 8000L;

  static {
    // TODO Remove this when external host checker has been designed (default/programmatic flag?)
    JSch.setConfig("StrictHostKeyChecking", "no");
  }

  private final JSch jsch;
  private final SSHCredentialRepository credentialRepository;
  private final long executionTimeoutMillis;
  private final SSHMachineSessionConstructor sessionConstructor;

  /**
   * Create a client with the default configuration.
   */
  public MachineSessionClient() {
    this(new JSch(), SSHCredentialRepository::new, SSHMachineSession::new);
  }

  MachineSessionClient(
      JSch jsch,
      SSHCredentialRepositoryConstructor credentialRepositoryConstructor,
      SSHMachineSessionConstructor sessionConstructor) {
    this(jsch, credentialRepositoryConstructor.construct(jsch), sessionConstructor);
  }

  MachineSessionClient(
      JSch jsch,
      SSHCredentialRepository credentialRepository,
      SSHMachineSessionConstructor sessionConstructor) {
    this(jsch, credentialRepository, EXECUTION_TIMEOUT_MILLIS, sessionConstructor);
  }

  MachineSessionClient(
      JSch jsch,
      SSHCredentialRepository credentialRepository,
      long executionTimeoutMillis,
      SSHMachineSessionConstructor sessionConstructor) {
    this.jsch = requireNonNull(jsch);
    this.credentialRepository = requireNonNull(credentialRepository);
    this.executionTimeoutMillis = executionTimeoutMillis;
    this.sessionConstructor = requireNonNull(sessionConstructor);
  }

  @Override
  public MachineSession getSession(
      InetAddress host,
      Integer port,
      String username,
      MachineSessionAuthentication authentication) {
    return sessionConstructor.construct(
        credentialRepository::passwordMapper,
        executionTimeoutMillis,
        jsch,
        host,
        port,
        username,
        authentication);
  }

  /**
   * Generates a key pair for the identity with the given name.
   *
   * <p>Writes the contents of the corresponding public key to the specified output.
   *
   * @param identityName Name of the identity for which the key pair is generated.
   * @param publicKeyOutput Output to which the resulting key pair is written.
   *
   * @throws IOException In case an error occurs during the generation of the key pair or the
   *         writing of the public key.
   *         
   * @see MachineSessionAuthenticationContext#setIdentityOnly(MachineSessionIdentityName)
   */
  public void generateKeyPair(MachineSessionIdentityName identityName, OutputStream publicKeyOutput)
      throws IOException {
    credentialRepository.generateKeyPair(identityName, publicKeyOutput);
  }

  /**
   * Generates a key pair for an anonymous identity.
   *
   * <p>Writes the contents of the corresponding public key to the specified output.
   *
   * @param publicKeyOutput Output to which the resulting key pair is written.
   *
   * @throws IOException In case an error occurs during the generation of the key pair or the
   *         writing of the public key.
   */
  public void generateKeyPair(OutputStream publicKeyOutput) throws IOException {
    credentialRepository.generateKeyPair(publicKeyOutput);
  }

  /**
   * Adds the value of a password with the given name.
   *
   * @param passwordName The password name.
   * @param password The password value.
   *
   * @see MachineSessionAuthenticationContext#addPassword(MachineSessionPasswordName)
   */
  public void addPassword(MachineSessionPasswordName passwordName, byte[] password) {
    credentialRepository.addPassword(passwordName, password);
  }
}
