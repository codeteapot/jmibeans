package com.github.codeteapot.jmibeans.library.dns;

import static com.github.codeteapot.jmibeans.platform.Machine.facetGet;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.library.dns.catalog.DomainNameServerFacet;
import com.github.codeteapot.jmibeans.library.dns.catalog.DomainNamedFacet;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.net.InetAddress;

class RegisteredDomain {

  private final PlatformContext context;
  private final String name;
  private final MachineRef domainNameServerRef;
  private final MachineRef domainNamedRef;
  private final PropertyChangeListener addressChangeListener;

  private RegisteredDomain(
      PlatformContext context,
      String name,
      MachineRef domainNameServerRef,
      MachineRef domainNamedRef) {
    this.context = requireNonNull(context);
    this.name = requireNonNull(name);
    this.domainNameServerRef = requireNonNull(domainNameServerRef);
    this.domainNamedRef = requireNonNull(domainNamedRef);
    addressChangeListener = new PropertyChangeListenerProxy("address", this::addressChange);
  }

  boolean match(MachineRef domainNameServerRef, MachineRef domainNamedRef) {
    return domainNameServerRef.equals(this.domainNameServerRef)
        && domainNamedRef.equals(this.domainNamedRef);
  }

  boolean lost(MachineRef machineRef) {
    if (machineRef.equals(domainNameServerRef)) {
      leave();
      return true;
    }
    if (machineRef.equals(domainNamedRef)) {
      unregister();
      return true;
    }
    return false;
  }

  private void addressChange(PropertyChangeEvent event) {
    if (event.getOldValue() != null) {
      unregister();
    }
    ofNullable(event.getNewValue())
        .map(InetAddress.class::cast)
        .ifPresent(this::register);
  }

  private void register(InetAddress address, DomainNameServerFacet domainNameServerFacet) {
    domainNameServerFacet.register(name, address);
  }

  private void register(InetAddress address) {
    context.lookup(domainNameServerRef)
        .flatMap(facetGet(DomainNameServerFacet.class))
        .ifPresent(facet -> register(address, facet));
  }

  private void unregister() {
    context.lookup(domainNameServerRef)
        .flatMap(facetGet(DomainNameServerFacet.class))
        .ifPresent(facet -> facet.unregister(name));
  }

  private void leave() {
    context.lookup(domainNamedRef)
        .flatMap(facetGet(DomainNamedFacet.class))
        .ifPresent(facet -> facet.removePropertyChangeListener(addressChangeListener));
  }

  static RegisteredDomain register(
      PlatformContext context,
      MachineRef domainNameServerRef,
      MachineRef domainNamedRef,
      DomainNameServerFacet domainNameServerFacet,
      DomainNamedFacet domainNamedFacet) {
    RegisteredDomain registeredDomain = new RegisteredDomain(
        context,
        domainNamedFacet.getDomainName(),
        domainNameServerRef,
        domainNamedRef);
    domainNamedFacet.getAddress().ifPresent(address -> registeredDomain.register(
        address,
        domainNameServerFacet));
    domainNamedFacet.addPropertyChangeListener(registeredDomain.addressChangeListener);
    return registeredDomain;
  }
}
