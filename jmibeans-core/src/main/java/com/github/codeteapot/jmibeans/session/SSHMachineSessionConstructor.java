package com.github.codeteapot.jmibeans.session;

import com.jcraft.jsch.JSch;
import java.net.InetAddress;

@FunctionalInterface
interface SSHMachineSessionConstructor {

  SSHMachineSession construct(
      SSHMachineSessionPasswordMapper passwordMapper,
      long executionTimeoutMillis,
      JSch jsch,
      InetAddress host,
      Integer port,
      String username,
      MachineSessionAuthentication authentication);
}
