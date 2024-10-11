package com.github.codeteapot.jmibeans.library.dns.catalog;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineNetworkAddressBinding;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.util.Optional;
import java.util.Set;

// TODO DESIGN Must be an interface
public class DNSHost {

  private final String zoneName;
  private final MachineNetworkAddressBinding addressBinding;
  private final Set<String> names;

  DNSHost(String zoneName, MachineNetworkAddressBinding addressBinding, Set<String> names) {
    this.zoneName = requireNonNull(zoneName);
    this.addressBinding = requireNonNull(addressBinding);
    this.names = requireNonNull(names);
  }

  public Optional<InetAddress> getAddress() {
    return addressBinding.getAddress();
  }

  public Set<String> getNames() {
    return names;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    addressBinding.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    addressBinding.removePropertyChangeListener(listener);
  }

  String getZoneName() {
    return zoneName;
  }
}
