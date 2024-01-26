package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;

class JschMachineShellHostKey implements MachineShellHostKey {

  private final byte[] encoded;

  JschMachineShellHostKey(byte[] encoded) {
    this.encoded = requireNonNull(encoded);
  }

  @Override
  public byte[] getEncoded() {
    return encoded;
  }
}
