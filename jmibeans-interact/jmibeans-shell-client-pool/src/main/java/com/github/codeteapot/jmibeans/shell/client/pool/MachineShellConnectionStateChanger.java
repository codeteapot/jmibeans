package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

class MachineShellConnectionStateChanger {

  private final Consumer<MachineShellConnectionState> changeStateAction;
  private final MachineShellConnectionClosedStateConstructor closedStateConstructor;

  MachineShellConnectionStateChanger(
      Consumer<MachineShellConnectionState> changeStateAction,
      MachineShellConnectionClosedStateConstructor closedStateConstructor) {
    this.changeStateAction = requireNonNull(changeStateAction);
    this.closedStateConstructor = requireNonNull(closedStateConstructor);
  }

  void closed() {
    changeStateAction.accept(closedStateConstructor.construct(this));
  }
}
