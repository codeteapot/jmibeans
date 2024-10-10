package com.github.codeteapot.jmibeans.machine;

import static java.util.Objects.requireNonNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MachineNetworkAddressBinding implements PropertyChangeListener {

  private final MachineNetworkName networkName;
  private final Consumer<InetAddress> updateAction;
  private InetAddress address;

  public MachineNetworkAddressBinding(
      MachineNetworkName networkName,
      Consumer<InetAddress> updateAction) {
    this.networkName = requireNonNull(networkName);
    this.updateAction = requireNonNull(updateAction);
    address = null;
  }

  public void update(Set<MachineNetwork> networks) {
    updateNetworks(networks::stream);
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if ("networks".equals(event.getPropertyName())) {
      updateNetworks(() -> ((Set<?>) event.getNewValue())
          .stream()
          .filter(MachineNetwork.class::isInstance)
          .map(MachineNetwork.class::cast));
    }
  }

  private void updateNetworks(Supplier<Stream<MachineNetwork>> networks) {
    try {
      updateAddress(networks.get()
          .filter(network -> networkName.equals(network.getName()))
          .findAny()
          .map(this::networkFound)
          .orElseGet(this::networkNotFound)
          .getAddress());
    } catch (UnknownHostException e) {
      updateAddress(null);
    } catch (ClassCastException e) {
      // Ignore networks
    }
  }
  
  private void updateAddress(InetAddress newAddress) {
    if (!Objects.equals(address, newAddress)) {
      address = newAddress;
      updateAction.accept(address);
    }
  }

  private InetAddressSupplier networkFound(MachineNetwork network) {
    return () -> network.getAddress();
  }

  private InetAddressSupplier networkNotFound() {
    return () -> null;
  }
}
