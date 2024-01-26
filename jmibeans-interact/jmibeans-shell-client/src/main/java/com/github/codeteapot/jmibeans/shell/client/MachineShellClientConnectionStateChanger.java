package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

class MachineShellClientConnectionStateChanger {

  private final Consumer<MachineShellClientConnectionState> changeStateAction;
  private final MachineShellClientConnectionEventSource eventSource;

  MachineShellClientConnectionStateChanger(
      Consumer<MachineShellClientConnectionState> changeStateAction,
      MachineShellClientConnectionEventSource eventSource) {
    this.changeStateAction = requireNonNull(changeStateAction);
    this.eventSource = requireNonNull(eventSource);
  }

  void unavailable() {
    changeStateAction.accept(new MachineShellClientConnectionUnavailableState(this, eventSource));
  }
}
