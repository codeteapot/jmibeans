package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.function.Consumer;

class ManagedMachineBuilderContextStateChanger {

  private final Consumer<ManagedMachineBuilderContextState> changeStateAction;
  private final ManagedMachineBuilderContextFinishedStateConstructor finishedStateConstructor;

  ManagedMachineBuilderContextStateChanger(
      Consumer<ManagedMachineBuilderContextState> changeStateAction,
      ManagedMachineBuilderContextFinishedStateConstructor finishedStateConstructor) {
    this.changeStateAction = requireNonNull(changeStateAction);
    this.finishedStateConstructor = requireNonNull(finishedStateConstructor);
  }

  void finish(Set<Runnable> disposeTasks) {
    changeStateAction.accept(finishedStateConstructor.construct(this, disposeTasks));
  }
}
