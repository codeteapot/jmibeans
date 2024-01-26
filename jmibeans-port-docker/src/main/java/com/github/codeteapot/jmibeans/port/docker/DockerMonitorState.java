package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineProfileName;

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

  abstract void start(MachineProfileName profileName, DockerMachineAgent agent);

  abstract void stop();

  abstract void connect(MachineNetwork network);

  abstract void disconnect(MachineNetworkName networkName);
}
