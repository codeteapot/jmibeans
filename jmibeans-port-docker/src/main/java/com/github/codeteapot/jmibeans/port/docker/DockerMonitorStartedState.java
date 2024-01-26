package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineManager;
import java.util.logging.Logger;

class DockerMonitorStartedState extends DockerMonitorState {

  private static final Logger logger = getLogger(DockerMonitor.class.getName());

  private final MachineManager machineManager;
  private final DockerMachineAgent agent;

  DockerMonitorStartedState(
      DockerMonitorStateChanger stateChanger,
      DockerMonitorId monitorId,
      MachineManager machineManager,
      DockerMachineAgent agent) {
    super(stateChanger, monitorId);
    this.machineManager = requireNonNull(machineManager);
    this.agent = requireNonNull(agent);
  }

  @Override
  void start(DockerMonitorStartContext context) {
    logger.severe(new StringBuilder()
        .append("Starting already started monitor ").append(monitorId)
        .toString());
    stateChanger.invalid(monitorId);
  }

  @Override
  void stop() {
    machineManager.forget(monitorId.getMachineId());
    stateChanger.stopped(monitorId, machineManager);
  }

  @Override
  void connect(MachineNetwork network) {
    agent.connect(network);
  }

  @Override
  void disconnect(MachineNetworkName networkName) {
    agent.disconnect(networkName);
  }
}
