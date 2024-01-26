package com.github.codeteapot.jmibeans.shell.mutable;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.net.InetAddress;

class MachineShellConnectionFactoryProxy<F extends MachineShellConnectionFactory> {

  private final MachineShellConnectionFactoryLifecycle<F> lifecycle;
  private InetAddress address;
  private F instance;

  MachineShellConnectionFactoryProxy(MachineShellConnectionFactoryLifecycle<F> lifecycle) {
    this.lifecycle = requireNonNull(lifecycle);
    address = null;
    instance = null;
  }

  MachineShellConnection getConnection(String username) throws MachineShellException {
    if (instance == null) {
      throw new MachineShellException("Connection factory instance is not available");
    }
    return instance.getConnection(username);
  }

  public void setAddress(InetAddress address) {
    if (instance == null) {
      if (address != null) {
        instance = lifecycle.create(address);
      }
    } else {
      if (address == null) {
        lifecycle.dispose(instance);
      } else if (!address.equals(this.address)) {
        lifecycle.dispose(instance);
        instance = lifecycle.create(address);
      }
    }
    this.address = address;
  }
}
