package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.MachineProfileName;

class DockerMonitorStoppedState extends DockerMonitorState {

  private static final Logger logger = getLogger(DockerMonitor.class.getName());

  private final MachineManager machineManager;

  DockerMonitorStoppedState(
      DockerMonitorStateChanger stateChanger,
      DockerMonitorId monitorId,
      MachineManager machineManager) {
    super(stateChanger, monitorId);
    this.machineManager = requireNonNull(machineManager);
  }

  @Override
  void start(MachineProfileName profileName, DockerMachineAgent agent) {
    machineManager.accept(monitorId.getMachineId(), new DockerMachineLink(profileName, agent));
    stateChanger.changeState(new DockerMonitorStartedState(
        stateChanger,
        monitorId,
        machineManager,
        agent));
  }

  @Override
  void stop() {
    logger.severe(new StringBuilder()
        .append("Stopping already stopped monitor ").append(monitorId)
        .toString());
    stateChanger.changeState(new DockerMonitorInvalidState(stateChanger, monitorId));
  }

  @Override
  void connect(MachineNetwork network) {
    logger.severe(new StringBuilder()
        .append("Could not connect monitor ").append(monitorId)
        .append(" because it is stopped")
        .toString());
    stateChanger.changeState(new DockerMonitorInvalidState(stateChanger, monitorId));
  }

  @Override
  void disconnect(MachineNetworkName networkName) {
    logger.severe(new StringBuilder()
        .append("Could not disconnect monitor ").append(monitorId)
        .append(" because it is stopped")
        .toString());
    stateChanger.changeState(new DockerMonitorInvalidState(stateChanger, monitorId));
  }
}
