package com.github.codeteapot.jmibeans.library.dns.catalog;

import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;

public class DomainNameServerFacetFactory {

  public static final String DNS_REGISTRAR_USER = "dns-registrar";

  public DomainNameServerFacet getFacet(MachineShellConnectionFactory shellConnectionFactory) {
    return new DomainNameServerFacet(shellConnectionFactory);
  }
}
