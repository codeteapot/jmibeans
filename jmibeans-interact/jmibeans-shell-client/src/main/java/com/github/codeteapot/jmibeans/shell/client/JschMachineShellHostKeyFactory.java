package com.github.codeteapot.jmibeans.shell.client;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;

class JschMachineShellHostKeyFactory {

  JschMachineShellHostKeyFactory() {}

  MachineShellHostKey getHostKey(byte[] encoded) {
    return new JschMachineShellHostKey(encoded);
  }
}
