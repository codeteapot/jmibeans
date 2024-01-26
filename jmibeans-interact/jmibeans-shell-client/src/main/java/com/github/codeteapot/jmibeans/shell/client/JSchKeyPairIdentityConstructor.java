package com.github.codeteapot.jmibeans.shell.client;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;

@FunctionalInterface
interface JSchKeyPairIdentityConstructor {

  JSchKeyPairIdentity construct(
      JSchMachineShellClientEngine context,
      MachineShellIdentityName name,
      MachineShellPublicKeyType publicKeyType,
      int publicKeySize) throws Exception;
}
