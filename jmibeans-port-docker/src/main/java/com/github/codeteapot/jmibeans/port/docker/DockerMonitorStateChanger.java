package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.port.MachineManager;
import java.util.function.Consumer;

class DockerMonitorStateChanger {

  private final Consumer<DockerMonitorState> changeStateAction;

  DockerMonitorStateChanger(Consumer<DockerMonitorState> changeStateAction) {
    this.changeStateAction = requireNonNull(changeStateAction);
  }

  void started(DockerMonitorId monitorId, MachineManager machineManager, DockerMachineAgent agent) {
    changeStateAction.accept(new DockerMonitorStartedState(this, monitorId, machineManager, agent));
  }

  void stopped(DockerMonitorId monitorId, MachineManager machineManager) {
    changeStateAction.accept(new DockerMonitorStoppedState(this, monitorId, machineManager));
  }

  void invalid(DockerMonitorId monitorId) {
    changeStateAction.accept(new DockerMonitorInvalidState(this, monitorId));
  }
}
