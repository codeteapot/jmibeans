package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperties;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import java.util.logging.Logger;

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
  void start(DockerMonitorStartContext context) {
    context.getProfileName()
        .map(profileName -> startWithProfile(
            profileName,
            context.getBuilderProperties(),
            context.getAgent()))
        .orElseGet(() -> startWithoutProfile())
        .run();
  }

  @Override
  void stop() {
    logger.severe(new StringBuilder()
        .append("Stopping already stopped monitor ").append(monitorId)
        .toString());
    stateChanger.invalid(monitorId);
  }

  @Override
  void connect(MachineNetwork network) {
    logger.severe(new StringBuilder()
        .append("Could not connect monitor ").append(monitorId)
        .append(" because it is stopped")
        .toString());
    stateChanger.invalid(monitorId);
  }

  @Override
  void disconnect(MachineNetworkName networkName) {
    logger.severe(new StringBuilder()
        .append("Could not disconnect monitor ").append(monitorId)
        .append(" because it is stopped")
        .toString());
    stateChanger.invalid(monitorId);
  }

  private Runnable startWithProfile(
      MachineProfileName profileName,
      MachineBuilderProperties builderProperties,
      DockerMachineAgent agent) {
    return () -> {
      machineManager.accept(
          monitorId.getMachineId(),
          new DockerMachineLink(profileName, builderProperties, agent));
      stateChanger.started(monitorId, machineManager, agent);
    };
  }

  private Runnable startWithoutProfile() {
    return () -> {
      logger.severe(new StringBuilder()
          .append("Could not start monitor ").append(monitorId)
          .append(" because profile could not be determined")
          .toString());
      stateChanger.invalid(monitorId);
    };
  }
}
