package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.security.auth.callback.CallbackHandler;

public class MachineShellClientContext {

  private static final int CONNECTION_TIMEOUT_MILLIS = 20000;
  private static final long EXECUTION_TIMEOUT_MILLIS = 8000L;

  private final JschMachineShellClientImplementationContext implementation;
  private final Set<JschKeyPairIdentity> identities;
  private final JschKeyPairIdentityConstructor identityConstructor;
  private final Supplier<Long> executionTimeoutMillisSupplier;

  public MachineShellClientContext(CallbackHandler callbackHandler) {
    this(
        new JschMachineShellClientImplementationContext(callbackHandler),
        new HashSet<>(),
        () -> EXECUTION_TIMEOUT_MILLIS,
        JschKeyPairIdentity::new);
  }

  MachineShellClientContext(
      JschMachineShellClientImplementationContext implementation,
      Set<JschKeyPairIdentity> identities,
      Supplier<Long> executionTimeoutMillisSupplier,
      JschKeyPairIdentityConstructor identityConstructor) {
    this.implementation = requireNonNull(implementation);
    this.identities = requireNonNull(identities);
    this.executionTimeoutMillisSupplier = requireNonNull(executionTimeoutMillisSupplier);
    this.identityConstructor = requireNonNull(identityConstructor);
  }

  public MachineShellClientConnection getConnection(MachineShellClientContextConnectionSpec spec)
      throws MachineShellClientException {
    try {
      Session jschSession = spec.getPort()
          .map(implementation::sessionWithCustomPort)
          .orElseGet(implementation::sessionWithDefaultPort)
          .map(spec.getUsername(), spec.getHostAddress());
      jschSession.setIdentityRepository(new JschKeyPairIdentityRepository(identities.stream()
          .filter(identity -> spec.getIdentityOnly()
              .map(identity::match)
              .orElse(true))
          .collect(toSet())));
      spec.getPassword()
          .map(String::new)
          .ifPresent(jschSession::setPassword);
      jschSession.connect(CONNECTION_TIMEOUT_MILLIS);
      return new JschMachineShellClientConnection(
          jschSession,
          executionTimeoutMillisSupplier.get());
    } catch (JSchException e) {
      throw new MachineShellClientException(e);
    }
  }

  public void generateIdentity(
      MachineShellIdentityName identityName,
      MachineShellPublicKey publicKey) throws MachineShellClientException, IOException {
    try {
      JschKeyPairIdentity identity = identityConstructor.construct(
          implementation,
          identityName,
          publicKey.type,
          publicKey.size);
      identity.writePublicKey(publicKey.output);
      identities.add(identity);
    } catch (Exception e) {
      throw new MachineShellClientException(e);
    }
  }
}
