package com.github.codeteapot.jmibeans.library.dns.catalog;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import java.net.InetAddress;

public class DNSServerFacet {

  private final MachineShellConnectionFactory connectionFactory;

  DNSServerFacet(MachineShellConnectionFactory connectionFactory) {
    this.connectionFactory = requireNonNull(connectionFactory);
  }

  public void createZone(String zoneName) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void register(String zoneName, String hostName, InetAddress address) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void unregister(String zoneName, String hostName) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
