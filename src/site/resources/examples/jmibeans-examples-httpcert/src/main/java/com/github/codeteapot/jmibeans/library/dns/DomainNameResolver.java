package com.github.codeteapot.jmibeans.library.dns;

import static com.github.codeteapot.jmibeans.library.dns.RegisteredDomain.register;
import static com.github.codeteapot.jmibeans.platform.Machine.facetGet;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.library.dns.catalog.DomainNameServerFacet;
import com.github.codeteapot.jmibeans.library.dns.catalog.DomainNamedFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.platform.event.PlatformListener;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class DomainNameResolver implements PlatformListener {

  private final PlatformContext context;
  private MachineRef serverRef;

  public DomainNameResolver(PlatformContext context) {
    this.context = requireNonNull(context);
  }

  @Override
  public void machineAvailable(MachineAvailableEvent event) {
    context.lookup(event.getMachineRef())
        .flatMap(facetGet(DomainNameServerFacet.class))
        .map(domainNameServerFacet -> context.available()
            .filter(machine -> registeredDomains.stream()
                .noneMatch(registeredDomain -> registeredDomain.match(
                    event.getMachineRef(),
                    machine.getRef())))
            .flatMap(machine -> machine.getFacet(DomainNamedFacet.class)
                .map(domainNamedFacet -> register(
                    context,
                    event.getMachineRef(),
                    machine.getRef(),
                    domainNameServerFacet,
                    domainNamedFacet))
                .map(Stream::of)
                .orElseGet(Stream::empty)))
        .orElseGet(Stream::empty)
        .forEach(registeredDomains::add);
    context.lookup(event.getMachineRef())
        .flatMap(facetGet(DomainNamedFacet.class))
        .map(domainNamedFacet -> context.available()
            .filter(machine -> registeredDomains.stream()
                .noneMatch(registeredDomain -> registeredDomain.match(
                    machine.getRef(),
                    event.getMachineRef())))
            .flatMap(machine -> machine.getFacet(DomainNameServerFacet.class)
                .map(domainNameServerFacet -> register(
                    context,
                    machine.getRef(),
                    event.getMachineRef(),
                    domainNameServerFacet,
                    domainNamedFacet))
                .map(Stream::of)
                .orElseGet(Stream::empty)))
        .orElseGet(Stream::empty)
        .forEach(registeredDomains::add);
  }

  @Override
  public void machineLost(MachineLostEvent event) {
    // registeredDomains.removeIf(registeredDomain -> registeredDomain.lost(event.getMachineRef()));
  }
}
