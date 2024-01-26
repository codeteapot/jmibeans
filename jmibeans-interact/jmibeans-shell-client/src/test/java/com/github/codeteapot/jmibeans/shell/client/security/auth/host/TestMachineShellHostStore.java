package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class TestMachineShellHostStore implements MachineShellHostStore {

  private final Map<InetAddress, MachineShellHost> hostMap;

  TestMachineShellHostStore() {
    hostMap = new HashMap<>();
  }

  @Override
  public Optional<MachineShellHost> get(InetAddress address) {
    return Optional.ofNullable(hostMap.get(address));
  }

  @Override
  public void add(MachineShellHost host) {
    hostMap.put(host.getAddress(), host);
  }
}
