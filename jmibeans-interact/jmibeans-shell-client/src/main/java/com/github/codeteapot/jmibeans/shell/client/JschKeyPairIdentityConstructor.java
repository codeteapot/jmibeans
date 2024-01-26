package com.github.codeteapot.jmibeans.shell.client;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;

@FunctionalInterface
interface JschKeyPairIdentityConstructor {

  JschKeyPairIdentity construct(
      JschMachineShellClientImplementationContext context,
      MachineShellIdentityName name,
      MachineShellPublicKeyType publicKeyType,
      int publicKeySize) throws Exception;
}
