package com.github.codeteapot.jmibeans.library.dns.catalog;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetworkAddressBinding;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.InetAddress;
import java.util.Optional;
import java.util.function.Supplier;

public class DomainNamedFacet {

  private final PropertyChangeSupport propertyChangeSupport;
  private final Supplier<String> domainNameSupplier;
  private InetAddress address;

  DomainNamedFacet(
      MachineBuilderContext builderContext,
      MachineNetworkName networkName,
      Supplier<String> domainNameSupplier) {
    propertyChangeSupport = new PropertyChangeSupport(this);
    this.domainNameSupplier = requireNonNull(domainNameSupplier);
    MachineNetworkAddressBinding addressBinding = new MachineNetworkAddressBinding(
        networkName,
        this::setAddress);
    MachineAgent agent = builderContext.getAgent();
    addressBinding.update(agent.getNetworks());
    builderContext.addDisposeAction(() -> agent.removePropertyChangeListener(addressBinding));
    agent.addPropertyChangeListener(addressBinding);
  }

  public String getDomainName() {
    return domainNameSupplier.get();
  }

  public Optional<InetAddress> getAddress() {
    return ofNullable(address);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  private void setAddress(InetAddress address) {
    InetAddress oldAddress = this.address;
    this.address = address;
    propertyChangeSupport.firePropertyChange("address", oldAddress, this.address);
  }
}
