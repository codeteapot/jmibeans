package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

abstract class PooledMachineShellClientConnectionState {

  private final PooledMachineShellClientConnectionClosedStateConstructor closedStateConstructor;
  private final PooledMachineShellClientConnectionErrorOccurredStateConstructor errStateConstructor;

  protected final PooledMachineShellClientConnectionStateChanger stateChanger;

  protected PooledMachineShellClientConnectionState(
      PooledMachineShellClientConnectionStateChanger stateChanger) {
    this(
        stateChanger,
        PooledMachineShellClientConnectionClosedState::new,
        PooledMachineShellClientConnectionErrorOccurredState::new);
  }

  PooledMachineShellClientConnectionState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      PooledMachineShellClientConnectionClosedStateConstructor closedStateConstructor,
      PooledMachineShellClientConnectionErrorOccurredStateConstructor errStateConstructor) {
    this.stateChanger = requireNonNull(stateChanger);
    this.closedStateConstructor = requireNonNull(closedStateConstructor);
    this.errStateConstructor = requireNonNull(errStateConstructor);
  }

  abstract boolean request(String username);

  abstract void acquire();

  abstract <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException;

  abstract MachineShellClientFile file(String path) throws MachineShellClientException;

  abstract void closeNow();

  abstract void release();

  void onClosed() {
    stateChanger.changeState(closedStateConstructor.construct(stateChanger, supplier()));
  }

  void onClosed(MachineShellClientException exception) {
    stateChanger.changeState(closedStateConstructor.construct(stateChanger, supplier(exception)));
  }

  void onErrorOccurred() {
    stateChanger.changeState(errStateConstructor.construct(stateChanger, supplier()));
  }

  void onErrorOccurred(MachineShellClientException exception) {
    stateChanger.changeState(errStateConstructor.construct(stateChanger, supplier(exception)));
  }

  private static Supplier<Optional<Exception>> supplier() {
    return Optional::empty;
  }

  private static Supplier<Optional<Exception>> supplier(Exception exception) {
    return () -> Optional.of(exception);
  }
}
