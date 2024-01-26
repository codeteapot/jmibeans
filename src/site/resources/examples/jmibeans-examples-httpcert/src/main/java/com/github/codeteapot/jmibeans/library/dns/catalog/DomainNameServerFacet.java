package com.github.codeteapot.jmibeans.library.dns.catalog;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import java.net.InetAddress;

public class DomainNameServerFacet {

  private final MachineShellConnectionFactory shellConnectionFactory;

  DomainNameServerFacet(MachineShellConnectionFactory shellConnectionFactory) {
    this.shellConnectionFactory = requireNonNull(shellConnectionFactory);
  }

  public void register(String domainName, InetAddress address) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void unregister(String domainName) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
