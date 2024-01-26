package com.github.codeteapot.jmibeans.shell.provider;

import static com.github.codeteapot.jmibeans.profile.MachineNetworkBinding.receiveAddressOrNull;
import static java.util.Objects.requireNonNull;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import com.github.codeteapot.jmibeans.profile.MachineNetworkBinding;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;

public class MachineShellProvider {

  private final MachineBuilderContext builderContext;
  private final MachineNetworkName networkName;
  private final MachineShellConnectionFactoryLifecycle<?> connectionFactoryLifecycle;

  public MachineShellProvider(
      MachineBuilderContext builderContext,
      MachineNetworkName networkName,
      MachineShellConnectionFactoryLifecycle<?> connectionFactoryLifecycle) {
    this.builderContext = requireNonNull(builderContext);
    this.networkName = requireNonNull(networkName);
    this.connectionFactoryLifecycle = requireNonNull(connectionFactoryLifecycle);
  }

  public MachineShellConnectionFactory getConnectionFactory() {
    MutableAddressMachineShellConectionFactory conectionFactory =
        new MutableAddressMachineShellConectionFactory(connectionFactoryLifecycle);
    builderContext.addDisposeTask(new MachineNetworkBinding(
        builderContext.getAgent(),
        networkName,
        receiveAddressOrNull(conectionFactory::setAddress))::unbind);
    return conectionFactory;
  }
}
