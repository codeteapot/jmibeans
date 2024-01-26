package com.github.codeteapot.jmibeans.depot.dns;

import static com.github.codeteapot.jmibeans.platform.Machine.facetFilter;
import static com.github.codeteapot.jmibeans.platform.Machine.facetGet;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSHostFacet;
import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSHostName;
import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSServerFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.platform.event.PlatformListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO SEGREGATE Module to jmibeans-library-dns with test machine images through Packer
public class DNSResolver implements PlatformListener {

  private final PlatformContext context;
  private final DNSLayout layout;
  private final Map<MachineRef, Set<DNSHostName>> knownHostsMap;

  public DNSResolver(PlatformContext context, DNSLayout layout) {
    this.context = requireNonNull(context);
    this.layout = requireNonNull(layout);
    knownHostsMap = new HashMap<>();
  }

  @Override
  public void machineAvailable(MachineAvailableEvent event) {
    context.lookup(event.getMachineRef())
        .flatMap(facetGet(DNSServerFacet.class))
        .ifPresent(serverFacet -> layout.getZones()
            .forEach(layoutZone -> serverFacet.enableZone(
                layoutZone.getName(),
                zone -> context.available()
                    .flatMap(facetFilter(DNSHostFacet.class))
                    .map(DNSHostFacet::getHosts)
                    .flatMap(Set::stream))));
    context.lookup(event.getMachineRef())
        .flatMap(facetGet(DNSHostFacet.class))
        .ifPresent(hostFacet -> layout.getZones()
            .forEach(layoutZone -> context.available()
                .flatMap(facetFilter(DNSServerFacet.class))
                .forEach(serverFacet -> hostFacet.getHosts()
                    .stream()
                    .peek(host -> knownHostsMap.computeIfAbsent(
                        event.getMachineRef(),
                        key -> new HashSet<>()).add(host.getName()))
                    .forEach(serverFacet::attach))));
  }

  @Override
  public void machineLost(MachineLostEvent event) {
    ofNullable(knownHostsMap.remove(event.getMachineRef()))
        .ifPresent(hostNames -> context.available()
            .flatMap(facetFilter(DNSServerFacet.class))
            .forEach(serverFacet -> hostNames.forEach(serverFacet::detach)));
  }
}
