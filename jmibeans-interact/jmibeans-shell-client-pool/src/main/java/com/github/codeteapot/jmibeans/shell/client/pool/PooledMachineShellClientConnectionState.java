package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import java.nio.file.FileSystem;
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

  abstract FileSystem getFileSystem() throws MachineShellClientException;

  abstract <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException;

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
