package com.github.codeteapot.jmibeans.profile;

import static java.util.Objects.requireNonNull;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;

public class MachineNetworkBinding {

  private static final String NETWORKS_PROPERTY_NAME = "networks";

  private final MachineNetworkName networkName;
  private final Runnable unbindAction;

  public MachineNetworkBinding(
      MachineAgent agent,
      MachineNetworkName networkName,
      Consumer<MachineNetwork> receiver) {
    this.networkName = requireNonNull(networkName);
    receiver.accept(networkFind(agent.getNetworks()::stream, networkName));
    PropertyChangeListener listener = new PropertyChangeListenerProxy(
        NETWORKS_PROPERTY_NAME,
        event -> newtorksPropertyChange(event, receiver));
    unbindAction = () -> agent.removePropertyChangeListener(listener);
    agent.addPropertyChangeListener(listener);
  }

  public void unbind() {
    unbindAction.run();
  }

  public static Consumer<MachineNetwork> receiveAddressOrNull(Consumer<InetAddress> receiver) {
    return network -> {
      try {
        if (network == null) {
          receiver.accept(null);
        } else {
          receiver.accept(network.getAddress());
        }
      } catch (UnknownHostException e) {
        receiver.accept(null);
      }
    };
  }

  private void newtorksPropertyChange(
      PropertyChangeEvent event,
      Consumer<MachineNetwork> receiver) {
    try {
      MachineNetwork oldNetwork = networkFind(networksCast(event.getOldValue()), networkName);
      MachineNetwork newNetwork = networkFind(networksCast(event.getNewValue()), networkName);
      if (hasChanges(oldNetwork, newNetwork)) {
        receiver.accept(newNetwork);
      }
    } catch (ClassCastException e) {
      // Ignore networks
    }
  }

  private static MachineNetwork networkFind(
      Supplier<Stream<MachineNetwork>> networks,
      MachineNetworkName networkName) {
    return networks.get()
        .filter(network -> networkName.equals(network.getName()))
        .findAny()
        .orElse(null);
  }

  private static Supplier<Stream<MachineNetwork>> networksCast(Object obj) {
    return () -> ((Set<?>) obj)
        .stream()
        .filter(MachineNetwork.class::isInstance)
        .map(MachineNetwork.class::cast);
  }

  private static boolean hasChanges(MachineNetwork oldNetwork, MachineNetwork newNetwork) {
    try {
      if (oldNetwork == null) {
        return newNetwork != null;
      }
      if (newNetwork == null) {
        return true;
      }
      if (!Objects.equals(oldNetwork.getAddress(), newNetwork.getAddress())) {
        return true;
      }
      if (!Objects.equals(oldNetwork.getPrefixLength(), newNetwork.getPrefixLength())) {
        return true;
      }
      return false;
    } catch (UnknownHostException e) {
      return true;
    }
  }
}
