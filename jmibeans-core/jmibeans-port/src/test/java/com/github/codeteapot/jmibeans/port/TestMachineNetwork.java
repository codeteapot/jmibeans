package com.github.codeteapot.jmibeans.port;

import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import java.net.InetAddress;
import java.net.UnknownHostException;

class TestMachineNetwork extends AbstractMachineNetwork {

  TestMachineNetwork(MachineNetworkName name) {
    super(name);
  }

  @Override
  public InetAddress getAddress() throws UnknownHostException {
    throw new UnsupportedOperationException();
  }

  @Override
  public short getPrefixLength() {
    throw new UnsupportedOperationException();
  }
}
