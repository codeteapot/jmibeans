package com.github.codeteapot.jmibeans.machine;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MachineNetworkAddressBinding implements PropertyChangeListener {

  private final PropertyChangeSupport propertyChangeSupport;
  private final MachineNetworkName networkName;
  private InetAddress address;
  private UnknownHostException exception;

  public MachineNetworkAddressBinding(
      MachineNetworkName networkName,
      Set<MachineNetwork> networks) {
    propertyChangeSupport = new PropertyChangeSupport(this);
    this.networkName = requireNonNull(networkName);
    address = addressFind(
        networks::stream,
        e -> exception = e); // PRE propertyChangeSupport and networkName are set
  }

  public MachineNetworkName getNetworkName() {
    return networkName;
  }

  public Optional<InetAddress> getAddress() {
    return ofNullable(address);
  }

  public Optional<UnknownHostException> getException() {
    return ofNullable(exception);
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if ("networks".equals(event.getPropertyName())) {
      try {
        InetAddress oldAddress = address;
        InetAddress address = addressFind(
            () -> ((Set<?>) event.getNewValue())
                .stream()
                .filter(MachineNetwork.class::isInstance)
                .map(MachineNetwork.class::cast),
            e -> {
              UnknownHostException oldException = exception;
              exception = e;
              propertyChangeSupport.firePropertyChange("exception", oldException, exception);
            });
        if (!Objects.equals(oldAddress, address)) {
          propertyChangeSupport.firePropertyChange("address", oldAddress, address);
        }
      } catch (ClassCastException e) {
        // Ignore networks
      }
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  private InetAddress addressFind(
      Supplier<Stream<MachineNetwork>> networks,
      Consumer<UnknownHostException> exceptionHandler) {
    return networks.get()
        .filter(network -> networkName.equals(network.getName()))
        .map(network -> knownAdressOrNull(network, exceptionHandler))
        .findAny()
        .orElse(null);
  }

  private InetAddress knownAdressOrNull(
      MachineNetwork network,
      Consumer<UnknownHostException> exceptionHandler) {
    try {
      return network.getAddress();
    } catch (UnknownHostException e) {
      exceptionHandler.accept(e);
      return null;
    }
  }
}
