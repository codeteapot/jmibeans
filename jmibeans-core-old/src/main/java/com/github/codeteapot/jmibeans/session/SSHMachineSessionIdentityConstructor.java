package com.github.codeteapot.jmibeans.session;

import com.jcraft.jsch.KeyPair;
import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
interface SSHMachineSessionIdentityConstructor {

  SSHMachineSessionIdentity construct(
      KeyPair keyPair,
      OutputStream publicKeyOutput,
      TempFileCreator tempFileCreator) throws IOException;
}
