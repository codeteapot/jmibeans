package com.github.codeteapot.jmibeans.depot.ca.catalog;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import java.util.Set;

public class CertificateHolderFacet {

  private final Set<FileSystemCertificateHolder> holders;

  public CertificateHolderFacet(
      MachineShellConnectionFactory connectionFactory,
      Set<CertificateHolderSettings> holders) {
    this.holders = holders.stream()
        .map(holder -> holder.create(connectionFactory))
        .collect(toSet());
  }

  public Set<CertificateHolder> getHolders() {
    return unmodifiableSet(holders);
  }
}
