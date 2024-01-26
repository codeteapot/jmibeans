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

class PooledMachineShellClientConnectionClosedState
    extends PooledMachineShellClientConnectionState {

  private static final Logger logger = getLogger(
      PooledMachineShellClientConnection.class.getName());

  private final Supplier<Optional<Exception>> exceptionSupplier;

  PooledMachineShellClientConnectionClosedState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      Supplier<Optional<Exception>> exceptionSupplier) {
    super(stateChanger);
    this.exceptionSupplier = requireNonNull(exceptionSupplier);
  }

  @Override
  boolean request(String username) {
    throw new IllegalStateException("Connection closed");
  }

  @Override
  void acquire() {
    throw new IllegalStateException("Connection closed");
  }

  @Override
  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    throw exceptionSupplier.get()
        .map(cause -> new MachineShellClientException("Connection closed with error", cause))
        .orElseGet(() -> new MachineShellClientException("Connection closed"));
  }

  @Override
  MachineShellClientFile file(String path) throws MachineShellClientException {
    throw exceptionSupplier.get()
        .map(cause -> new MachineShellClientException("Connection closed with error", cause))
        .orElseGet(() -> new MachineShellClientException("Connection closed"));
  }

  @Override
  void closeNow() {
    throw new IllegalStateException("Connection already closed");
  }

  @Override
  void release() {
    throw new IllegalStateException("Connection closed");
  }

  @Override
  void onClosed() {
    logger.warning("Connection was already closed");
  }

  @Override
  void onClosed(MachineShellClientException exception) {
    logger.warning("Connection was already closed");
  }

  @Override
  void onErrorOccurred() {
    logger.warning("Connection closed");
  }

  @Override
  void onErrorOccurred(MachineShellClientException exception) {
    logger.warning("Connection closed");
  }
}
