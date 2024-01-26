package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;

abstract class DockerMonitorState {

  protected final DockerMonitorStateChanger stateChanger;
  protected final DockerMonitorId monitorId;

  protected DockerMonitorState(DockerMonitorStateChanger stateChanger, DockerMonitorId monitorId) {
    this.stateChanger = requireNonNull(stateChanger);
    this.monitorId = requireNonNull(monitorId);
  }

  DockerMonitorId getMonitorId() {
    return monitorId;
  }

  abstract void start(DockerMonitorStartContext context);

  abstract void stop();

  abstract void connect(MachineNetwork network);

  abstract void disconnect(MachineNetworkName networkName);
}
