package com.github.codeteapot.jmibeans.machine;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class MachineNetworkBinding implements PropertyChangeListener {

  private final PropertyChangeSupport propertyChangeSupport;
  private final MachineNetworkName networkName;
  private MachineNetwork network;

  public MachineNetworkBinding(MachineNetworkName networkName, Set<MachineNetwork> networks) {
    propertyChangeSupport = new PropertyChangeSupport(this);
    this.networkName = requireNonNull(networkName);
    network = networks.stream()
        .filter(this::matchNetworkName) // PRE networkName is set
        .findAny()
        .orElse(null);
  }

  public MachineNetworkName getNetworkName() {
    return networkName;
  }

  public Optional<MachineNetwork> getNetwork() {
    return ofNullable(network);
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if ("networks".equals(event.getPropertyName())) {
      MachineNetwork newNetwork = ((Set<?>) event.getNewValue())
          .stream()
          .filter(MachineNetwork.class::isInstance)
          .map(MachineNetwork.class::cast)
          .filter(this::matchNetworkName)
          .findAny()
          .orElse(null);
      if (!Objects.equals(network, newNetwork)) {
        MachineNetwork oldNetwork = network;
        network = newNetwork;
        propertyChangeSupport.firePropertyChange("network", oldNetwork, network);
      }
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  private boolean matchNetworkName(MachineNetwork network) {
    return networkName.equals(network.getName());
  }
}
