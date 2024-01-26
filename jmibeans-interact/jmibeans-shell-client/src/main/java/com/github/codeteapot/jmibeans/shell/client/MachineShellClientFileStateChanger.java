package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

class MachineShellClientFileStateChanger {

  private final Consumer<MachineShellClientFileState> changeStateAction;

  MachineShellClientFileStateChanger(Consumer<MachineShellClientFileState> changeStateAction) {
    this.changeStateAction = requireNonNull(changeStateAction);
  }

  void detached() {
    changeStateAction.accept(new MachineShellClientFileDetachedState(this));
  }
}
