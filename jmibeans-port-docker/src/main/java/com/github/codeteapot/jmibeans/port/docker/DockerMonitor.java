package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineManager;
import java.util.function.Consumer;
import java.util.logging.Logger;

class DockerMonitor {

  private static final Logger logger = getLogger(DockerMonitor.class.getName());

  private DockerMonitorState state;

  DockerMonitor(DockerMonitorId monitorId, MachineManager machineManager) {
    state = initial(newState -> state = requireNonNull(newState), monitorId, machineManager);
  }

  void start(DockerMonitorStartContext context) {
    state.start(context);
  }

  void stop() {
    state.stop();
  }

  void destroy() {
    logger.info(new StringBuilder()
        .append("Monitor ").append(state.getMonitorId()).append(" was destroyed")
        .toString());
  }

  void connect(MachineNetwork network) {
    state.connect(network);
  }

  void disconnect(MachineNetworkName networkName) {
    state.disconnect(networkName);
  }

  private static DockerMonitorState initial(
      Consumer<DockerMonitorState> changeStateAction,
      DockerMonitorId monitorId,
      MachineManager machineManager) {
    return new DockerMonitorStoppedState(
        new DockerMonitorStateChanger(changeStateAction),
        monitorId,
        machineManager);
  }
}
