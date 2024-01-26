package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineManager;

class PlatformAdapterMachineManager implements MachineManager, AutoCloseable {

  private final PlatformListenId listenId;
  private final MachineContainer container;

  PlatformAdapterMachineManager(
      PlatformListenIdGenerator listenIdGenerator,
      MachineContainer container) {
    listenId = listenIdGenerator.generate();
    this.container = requireNonNull(container);
  }

  @Override
  public void accept(byte[] machineId, MachineLink link) {
    container.accept(listenId.machineRef(machineId), link);
  }

  @Override
  public void forget(byte[] machineId) {
    container.forget(listenId.machineRef(machineId));
  }

  @Override
  public void close() {
    container.forgetAll(listenId::isParentOf);
  }
}
