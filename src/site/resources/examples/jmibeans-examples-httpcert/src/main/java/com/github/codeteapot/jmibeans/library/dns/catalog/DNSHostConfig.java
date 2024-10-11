package com.github.codeteapot.jmibeans.library.dns.catalog;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetworkAddressBinding;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class DNSHostConfig {

  private final String zoneName;
  private final MachineNetworkName networkName;
  private final Set<String> names;

  public DNSHostConfig(String zoneName, MachineNetworkName networkName, Set<String> names) {
    this.zoneName = requireNonNull(zoneName);
    this.networkName = requireNonNull(networkName);
    this.names = requireNonNull(names);
  }

  DNSHost getHost(
      Map<MachineNetworkName, MachineNetworkAddressBinding> addressBindingMap,
      MachineBuilderContext builderContext) {
    return new DNSHost(
        zoneName,
        addressBindingMap.computeIfAbsent(networkName, newAddressBinding(builderContext)),
        names);
  }

  private static Function<MachineNetworkName, MachineNetworkAddressBinding> newAddressBinding(
      MachineBuilderContext builderContext) {
    return networkName -> {
      MachineAgent agent = builderContext.getAgent();
      MachineNetworkAddressBinding addressBinding = new MachineNetworkAddressBinding(
          networkName,
          agent.getNetworks());
      builderContext.addDisposeAction(() -> agent.removePropertyChangeListener(addressBinding));
      agent.addPropertyChangeListener(addressBinding);
      return addressBinding;
    };
  }
}
