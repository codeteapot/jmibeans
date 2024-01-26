package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineDisposeAction;
import java.util.Set;
import java.util.function.Consumer;

class ManagedMachineStateChanger {

  private final Consumer<ManagedMachineState> changeStateAction;
  private final ManagedMachineAvailableStateConstructor availableStateConstructor;
  private final ManagedMachineBuildingFailureStateConstructor buildingFailureStateConstructor;
  private final ManagedMachineDisposedStateConstructor disposedStateConstructor;

  ManagedMachineStateChanger(
      Consumer<ManagedMachineState> changeStateAction,
      ManagedMachineAvailableStateConstructor availableStateConstructor,
      ManagedMachineBuildingFailureStateConstructor buildingFailureStateConstructor,
      ManagedMachineDisposedStateConstructor disposedStateConstructor) {
    this.changeStateAction = requireNonNull(changeStateAction);
    this.availableStateConstructor = requireNonNull(availableStateConstructor);
    this.buildingFailureStateConstructor = requireNonNull(buildingFailureStateConstructor);
    this.disposedStateConstructor = requireNonNull(disposedStateConstructor);
  }

  void available(
      MachineRef ref,
      PlatformEventTarget eventTarget,
      Set<Object> facets,
      Set<MachineDisposeAction> disposeActions) {
    changeStateAction.accept(availableStateConstructor.construct(
        this,
        ref,
        eventTarget,
        facets,
        disposeActions));
  }

  void buildingFailure(MachineRef ref) {
    changeStateAction.accept(buildingFailureStateConstructor.construct(this, ref));
  }

  void disposed(MachineRef ref) {
    changeStateAction.accept(disposedStateConstructor.construct(this, ref));
  }
}
