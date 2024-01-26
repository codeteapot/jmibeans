package com.github.codeteapot.jmibeans.depot.dns.catalog;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import com.github.codeteapot.jmibeans.profile.MachineNetworkBinding;
import java.util.function.Consumer;

class DNSManagedHost implements DNSHost {

  private final MachineAgent agent;
  private final DNSHostName name;

  DNSManagedHost(MachineBuilderContext builderContext, DNSHostName name) {
    this.agent = builderContext.getAgent();
    this.name = requireNonNull(name);
  }

  @Override
  public DNSHostName getName() {
    return name;
  }

  @Override
  public MachineNetworkBinding networkBind(
      MachineNetworkName networkName,
      Consumer<MachineNetwork> receiver) {
    return new MachineNetworkBinding(agent, networkName, receiver);
  }
}
