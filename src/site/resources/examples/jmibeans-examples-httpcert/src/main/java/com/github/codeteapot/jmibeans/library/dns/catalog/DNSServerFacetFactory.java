package com.github.codeteapot.jmibeans.library.dns.catalog;

import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;

public class DNSServerFacetFactory {

  public static final String DNS_REGISTRAR_USER = "dns-registrar";

  public DNSServerFacet getFacet(MachineShellConnectionFactory connectionFactory) {
    return new DNSServerFacet(connectionFactory);
  }
}
