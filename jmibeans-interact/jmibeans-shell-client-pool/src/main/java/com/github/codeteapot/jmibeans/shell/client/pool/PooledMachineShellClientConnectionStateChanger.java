package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

class PooledMachineShellClientConnectionStateChanger {

  private final Consumer<PooledMachineShellClientConnectionState> changeStateAction;
  private final PooledMachineShellClientConnectionAcquiredStateConstructor acquiredStateConstructor;
  private final PooledMachineShellClientConnectionAvailableStateConstructor availStateConstructor;
  private final PooledMachineShellClientConnectionClosedStateConstructor closedStateConstructor;
  private final PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor;
  private final PooledMachineShellClientConnectionErrorOccurredStateConstructor errStateConstructor;
  private final PooledMachineShellClientConnectionRequestedStateConstructor reqStateConstructor;

  PooledMachineShellClientConnectionStateChanger(
      Consumer<PooledMachineShellClientConnectionState> changeStateAction,
      PooledMachineShellClientConnectionAcquiredStateConstructor acquiredStateConstructor,
      PooledMachineShellClientConnectionAvailableStateConstructor availStateConstructor,
      PooledMachineShellClientConnectionClosedStateConstructor closedStateConstructor,
      PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor,
      PooledMachineShellClientConnectionErrorOccurredStateConstructor errStateConstructor,
      PooledMachineShellClientConnectionRequestedStateConstructor reqStateConstructor) {
    this.changeStateAction = requireNonNull(changeStateAction);
    this.acquiredStateConstructor = requireNonNull(acquiredStateConstructor);
    this.availStateConstructor = requireNonNull(availStateConstructor);
    this.closedStateConstructor = requireNonNull(closedStateConstructor);
    this.closingStateConstructor = requireNonNull(closingStateConstructor);
    this.errStateConstructor = requireNonNull(errStateConstructor);
    this.reqStateConstructor = requireNonNull(reqStateConstructor);
  }

  void acquired(MachineShellClientConnectionBridge bridge, String username) {
    changeStateAction.accept(acquiredStateConstructor.construct(this, bridge, username));
  }

  void available(
      MachineShellClientConnectionBridge bridge,
      String username,
      MachineShellClientConnectionBridgeCloseTask closeTask) {
    changeStateAction.accept(availStateConstructor.construct(this, bridge, username, closeTask));
  }

  void closed(Supplier<Optional<Exception>> exceptionSupplier) {
    changeStateAction.accept(closedStateConstructor.construct(this, exceptionSupplier));
  }

  void closing() {
    changeStateAction.accept(closingStateConstructor.construct(this));
  }

  void errorOccurred(Supplier<Optional<Exception>> exceptionSupplier) {
    changeStateAction.accept(errStateConstructor.construct(this, exceptionSupplier));
  }

  void requested(MachineShellClientConnectionBridge bridge, String username) {
    changeStateAction.accept(reqStateConstructor.construct(this, bridge, username));
  }
}
