package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;
import java.io.OutputStream;

public class MachineShellPublicKey {

  final MachineShellPublicKeyType type;
  final int size;
  final OutputStream output;

  public MachineShellPublicKey(MachineShellPublicKeyType type, int size, OutputStream output) {
    this.type = requireNonNull(type);
    this.size = size;
    this.output = requireNonNull(output);
  }
}
