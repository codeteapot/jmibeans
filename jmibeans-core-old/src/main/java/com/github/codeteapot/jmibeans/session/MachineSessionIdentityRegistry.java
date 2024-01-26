package com.github.codeteapot.jmibeans.session;

import java.io.IOException;
import java.io.OutputStream;

interface MachineSessionIdentityRegistry {

  void generateKeyPair(
      MachineSessionIdentityName identityName,
      OutputStream publicKeyOuput) throws IOException;

  void generateKeyPair(OutputStream publicKeyOutput) throws IOException;
}
