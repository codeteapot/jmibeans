package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineManager;

class PlatformAdapterMachineManager implements MachineManager {

  private final PlatformPortId portId;
  private final MachineContainer container;

  PlatformAdapterMachineManager(
      PlatformPortIdGenerator portIdGenerator,
      MachineContainer container) {
    portId = portIdGenerator.generate();
    this.container = requireNonNull(container);
  }

  @Override
  public void accept(byte[] machineId, MachineLink link) {
    container.accept(portId.machineRef(machineId), link);
  }

  @Override
  public void forget(byte[] machineId) {
    container.forget(portId.machineRef(machineId));
  }
}
