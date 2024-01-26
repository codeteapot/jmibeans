package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.port.MachineId;
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
  public void accept(MachineId id, MachineLink link) {
    container.accept(portId.machineRef(id), link);
  }

  @Override
  public void forget(MachineId id) {
    container.forget(portId.machineRef(id));
  }
}
