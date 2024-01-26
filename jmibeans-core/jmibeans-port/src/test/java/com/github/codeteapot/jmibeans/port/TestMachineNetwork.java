package com.github.codeteapot.jmibeans.port;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.github.codeteapot.jmibeans.machine.MachineNetworkName;

class TestMachineNetwork extends AbstractMachineNetwork {

  TestMachineNetwork(MachineNetworkName name) {
    super(name);
  }

  @Override
  public InetAddress getAddress() throws UnknownHostException {
    throw new UnsupportedOperationException();
  }
}
