package com.github.codeteapot.jmibeans.session;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class SSHCredentialRepository {

  private final TempFileCreator tempFileCreator;
  private final JSch jsch;
  private final Set<SSHMachineSessionIdentity> unnamedIdentities;
  private final Map<MachineSessionIdentityName, SSHMachineSessionIdentity> namedIdentityMap;
  private final Map<MachineSessionPasswordName, byte[]> passwordMap;
  private final KeyPairGenerator keyPairGenerator;
  private final SSHMachineSessionIdentityConstructor sessionIdentityConstructor;

  SSHCredentialRepository(JSch jsch) {
    this(
        jsch,
        new HashSet<>(),
        new HashMap<>(),
        new HashMap<>(),
        KeyPair::genKeyPair,
        SSHMachineSessionIdentity::new);
  }

  SSHCredentialRepository(
      JSch jsch,
      Set<SSHMachineSessionIdentity> unnamedIdentities,
      Map<MachineSessionIdentityName, SSHMachineSessionIdentity> namedIdentityMap,
      Map<MachineSessionPasswordName, byte[]> passwordMap,
      KeyPairGenerator keyPairGenerator,
      SSHMachineSessionIdentityConstructor sessionIdentityConstructor) {
    tempFileCreator = new TempFileCreator();
    this.jsch = requireNonNull(jsch);
    this.unnamedIdentities = requireNonNull(unnamedIdentities);
    this.namedIdentityMap = requireNonNull(namedIdentityMap);
    this.passwordMap = requireNonNull(passwordMap);
    this.keyPairGenerator = requireNonNull(keyPairGenerator);
    this.sessionIdentityConstructor = requireNonNull(sessionIdentityConstructor);
  }

  void generateKeyPair(
      MachineSessionIdentityName identityName,
      OutputStream publicKeyOutput) throws IOException {
    namedIdentityMap.put(identityName, createIdentity(publicKeyOutput));
  }

  void generateKeyPair(OutputStream publicKeyOutput) throws IOException {
    unnamedIdentities.add(createIdentity(publicKeyOutput));
  }

  Optional<byte[]> passwordMapper(MachineSessionPasswordName passwordName) {
    return ofNullable(passwordMap.get(passwordName));
  }

  void addPassword(MachineSessionPasswordName passwordName, byte[] password) {
    passwordMap.put(passwordName, password);
  }

  private SSHMachineSessionIdentity createIdentity(OutputStream publicKeyOutput)
      throws IOException {
    try {
      SSHMachineSessionIdentity identity = sessionIdentityConstructor.construct(
          keyPairGenerator.generate(jsch, KeyPair.RSA, 2048),
          publicKeyOutput,
          tempFileCreator);
      identity.addTo(jsch);
      return identity;
    } catch (JSchException e) {
      throw new IOException(e);
    }
  }
}
