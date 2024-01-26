package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.MachineProfileName;

// TODO Interface on all state pattern cases in order to hide changeState method
class DockerMonitorImpl implements DockerMonitor, DockerMonitorStateChanger {

  private static final Logger logger = getLogger(DockerMonitor.class.getName());

  private DockerMonitorState state;

  DockerMonitorImpl(DockerMonitorId monitorId, MachineManager machineManager) {
    state = new DockerMonitorStoppedState(this, monitorId, machineManager);
  }

  @Override
  public void start(MachineProfileName profileName, DockerMachineAgent agent) {
    state.start(profileName, agent);
  }

  @Override
  public void stop() {
    state.stop();
  }

  @Override
  public void destroy() {
    logger.info(new StringBuilder()
        .append("Monitor ").append(state.getMonitorId()).append(" was destroyed")
        .toString());
  }

  @Override
  public void connect(MachineNetwork network) {
    state.connect(network);
  }

  @Override
  public void disconnect(MachineNetworkName networkName) {
    state.disconnect(networkName);
  }

  @Override
  public void changeState(DockerMonitorState newState) {
    state = requireNonNull(newState);
  }
}
