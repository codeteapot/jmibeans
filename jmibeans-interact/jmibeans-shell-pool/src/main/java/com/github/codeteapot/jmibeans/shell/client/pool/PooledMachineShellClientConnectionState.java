package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;
import java.util.Optional;
import java.util.function.Supplier;

abstract class PooledMachineShellClientConnectionState {

  protected final PooledMachineShellClientConnectionStateChanger stateChanger;

  protected PooledMachineShellClientConnectionState(
      PooledMachineShellClientConnectionStateChanger stateChanger) {
    this.stateChanger = requireNonNull(stateChanger);
  }

  abstract boolean request(String username);

  abstract void acquire();

  abstract <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException;

  abstract MachineShellClientFile file(String path) throws MachineShellClientException;

  abstract void closeNow();

  abstract void release();

  void onClosed() {
    stateChanger.closed(supplier());
  }

  void onClosed(MachineShellClientException exception) {
    stateChanger.closed(supplier(exception));
  }

  void onErrorOccurred() {
    stateChanger.errorOccurred(supplier());
  }

  void onErrorOccurred(MachineShellClientException exception) {
    stateChanger.errorOccurred(supplier(exception));
  }

  private static Supplier<Optional<Exception>> supplier() {
    return Optional::empty;
  }

  private static Supplier<Optional<Exception>> supplier(Exception exception) {
    return () -> Optional.of(exception);
  }
}
