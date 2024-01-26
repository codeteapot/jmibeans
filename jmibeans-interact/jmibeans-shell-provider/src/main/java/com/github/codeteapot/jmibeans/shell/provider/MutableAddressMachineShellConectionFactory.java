package com.github.codeteapot.jmibeans.shell.provider;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.net.InetAddress;

class MutableAddressMachineShellConectionFactory implements MachineShellConnectionFactory {

  private MachineShellConnectionFactoryProxy<?> proxy;

  MutableAddressMachineShellConectionFactory(
      MachineShellConnectionFactoryLifecycle<?> lifecycle) {
    proxy = new MachineShellConnectionFactoryProxy<>(lifecycle);
  }

  @Override
  public MachineShellConnection getConnection(String username) throws MachineShellException {
    return proxy.getConnection(username);
  }

  void setAddress(InetAddress address) {
    proxy.setAddress(address);
  }
}
