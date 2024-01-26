package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

class PooledMachineShellClientConnectionErrorOccurredState
    extends PooledMachineShellClientConnectionState {

  private static final Logger logger = getLogger(
      PooledMachineShellClientConnection.class.getName());

  private final Supplier<Optional<Exception>> exceptionSupplier;

  PooledMachineShellClientConnectionErrorOccurredState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      Supplier<Optional<Exception>> exceptionSupplier) {
    super(stateChanger);
    this.exceptionSupplier = requireNonNull(exceptionSupplier);
  }

  @Override
  boolean request(String username) {
    throw new IllegalStateException("Error occurred on connection");
  }

  @Override
  void acquire() {
    throw new IllegalStateException("Error occurred on connection");
  }

  @Override
  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    throw exceptionSupplier.get()
        .map(cause -> new MachineShellClientException("Error occurred on connection", cause))
        .orElseGet(() -> new MachineShellClientException("Error occurred on connection"));
  }

  @Override
  MachineShellClientFile file(String path) throws MachineShellClientException {
    throw exceptionSupplier.get()
        .map(cause -> new MachineShellClientException("Error occurred on connection", cause))
        .orElseGet(() -> new MachineShellClientException("Error occurred on connection"));
  }

  @Override
  void closeNow() {
    throw new IllegalStateException("Error occurred on connection");
  }

  @Override
  void release() {
    throw new IllegalStateException("Error occurred on connection");
  }

  @Override
  void onClosed() {
    logger.warning("Error occurred on connection");
  }

  @Override
  void onClosed(MachineShellClientException exception) {
    logger.warning("Error occurred on connection");
  }

  @Override
  void onErrorOccurred() {
    logger.warning("Error was already occurred on connection");
  }

  @Override
  void onErrorOccurred(MachineShellClientException exception) {
    logger.warning("Error was already occurred on connection");
  }
}
