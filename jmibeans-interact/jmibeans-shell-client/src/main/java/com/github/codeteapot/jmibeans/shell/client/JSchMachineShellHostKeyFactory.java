package com.github.codeteapot.jmibeans.shell.client;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;

class JSchMachineShellHostKeyFactory {

  JSchMachineShellHostKeyFactory() {}

  MachineShellHostKey getHostKey(byte[] encoded) {
    return new JSchMachineShellHostKey(encoded);
  }
}
