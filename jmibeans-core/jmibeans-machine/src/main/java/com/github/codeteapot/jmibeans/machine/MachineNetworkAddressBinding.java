package com.github.codeteapot.jmibeans.machine;

import static java.util.Objects.requireNonNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MachineNetworkAddressBinding implements PropertyChangeListener {

  private final MachineNetworkName networkName;
  private Consumer<InetAddress> updateAction;

  public MachineNetworkAddressBinding(
      MachineNetworkName networkName,
      Consumer<InetAddress> updateAction,
      Set<MachineNetwork> networks) {
    this.networkName = requireNonNull(networkName);
    this.updateAction = requireNonNull(updateAction);
    updateAddress(networks::stream);
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if ("networks".equals(event.getPropertyName())) {
      updateAddress(() -> ((Set<?>) event.getNewValue())
          .stream()
          .filter(MachineNetwork.class::isInstance)
          .map(MachineNetwork.class::cast));
    }
  }

  private void updateAddress(Supplier<Stream<MachineNetwork>> networks) {
    try {
      updateAction.accept(networks.get()
          .filter(network -> networkName.equals(network.getName()))
          .findAny()
          .map(this::networkFound)
          .orElseGet(this::networkNotFound)
          .getAddress());
    } catch (UnknownHostException e) {
      updateAction.accept(null);
    } catch (ClassCastException e) {
      // Ignore networks
    }
  }

  private InetAddressSupplier networkFound(MachineNetwork network) {
    return () -> network.getAddress();
  }

  private InetAddressSupplier networkNotFound() {
    return () -> null;
  }
}
