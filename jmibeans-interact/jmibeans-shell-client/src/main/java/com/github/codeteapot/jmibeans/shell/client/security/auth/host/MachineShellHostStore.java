package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import java.net.InetAddress;
import java.util.Optional;

public interface MachineShellHostStore {

  Optional<MachineShellHost> get(InetAddress address);

  void add(MachineShellHost host);
}
