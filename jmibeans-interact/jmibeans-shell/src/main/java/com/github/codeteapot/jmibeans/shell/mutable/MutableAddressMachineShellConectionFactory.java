package com.github.codeteapot.jmibeans.shell.mutable;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.net.InetAddress;

public class MutableAddressMachineShellConectionFactory implements MachineShellConnectionFactory {

  private MachineShellConnectionFactoryProxy<?> proxy;

  public MutableAddressMachineShellConectionFactory(
      MachineShellConnectionFactoryLifecycle<?> lifecycle) {
    proxy = new MachineShellConnectionFactoryProxy<>(lifecycle);
  }

  @Override
  public MachineShellConnection getConnection(String username) throws MachineShellException {
    return proxy.getConnection(username);
  }

  public void setAddress(InetAddress address) {
    proxy.setAddress(address);
  }
}
