package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.dockerjava.api.model.Container;

class DockerMachineAgent implements MachineAgent {

  private final PropertyChangeSupport propertyChangeSupport;
  private final Set<MachineNetwork> networks;

  DockerMachineAgent(Container container) {
    propertyChangeSupport = new PropertyChangeSupport(this);
    networks = container.getNetworkSettings()
        .getNetworks()
        .entrySet()
        .stream()
        .map(entry -> new DockerMachineNetwork(entry.getKey(), entry.getValue()))
        .collect(toSet());
  }

  @Override
  public Set<MachineNetwork> getNetworks() {
    return networks;
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  void connect(MachineNetwork network) {
    Set<MachineNetwork> oldNetworks = new HashSet<>(networks);
    networks.add(network);
    propertyChangeSupport.firePropertyChange(
        "networks",
        unmodifiableSet(oldNetworks),
        unmodifiableSet(networks));
  }

  void disconnect(MachineNetworkName networkName) {
    Set<MachineNetwork> oldNetworks = new HashSet<>(networks);
    networks.removeIf(network -> networkName.equals(network.getName()));
    propertyChangeSupport.firePropertyChange(
        "networks",
        unmodifiableSet(oldNetworks),
        unmodifiableSet(networks));
  }
}
