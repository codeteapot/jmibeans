package com.github.codeteapot.jmibeans.port.docker;

import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineProfileName;

class DockerMonitorInvalidState extends DockerMonitorState {

  private static final Logger logger = getLogger(DockerMonitor.class.getName());

  DockerMonitorInvalidState(DockerMonitorStateChanger stateChanger, DockerMonitorId monitorId) {
    super(stateChanger, monitorId);
  }

  @Override
  void start(MachineProfileName profileName, DockerMachineAgent agent) {
    logger.severe(new StringBuilder()
        .append("Monitor ").append(monitorId)
        .append(" could not start because it is in invalid state")
        .toString());
  }

  @Override
  void stop() {
    logger.severe(new StringBuilder()
        .append("Monitor ").append(monitorId)
        .append(" could not stop because it is in invalid state")
        .toString());
  }

  @Override
  void connect(MachineNetwork network) {
    logger.severe(new StringBuilder()
        .append("Monitor ").append(monitorId)
        .append(" could not be connected to the network ")
        .append(network.getName())
        .append(" because it is in invalid state")
        .toString());
  }

  @Override
  void disconnect(MachineNetworkName networkName) {
    logger.severe(new StringBuilder()
        .append("Monitor ").append(monitorId)
        .append(" could not be disconnected from the network ")
        .append(networkName)
        .append(" because it is in invalid state")
        .toString());
  }
}
