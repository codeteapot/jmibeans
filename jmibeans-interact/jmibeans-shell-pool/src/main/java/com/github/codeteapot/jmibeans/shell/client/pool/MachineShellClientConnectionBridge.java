package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import java.util.logging.Logger;

class MachineShellClientConnectionBridge {

  private static final Logger logger = getLogger(
      MachineShellClientConnectionBridge.class.getName());

  private final ScheduledExecutorService closeExecutor;
  private final Supplier<Duration> idleTimeoutSupplier;
  private final MachineShellClientConnection connection;

  MachineShellClientConnectionBridge(
      ScheduledExecutorService closeExecutor,
      Supplier<Duration> idleTimeoutSupplier,
      MachineShellClientConnection connection) {
    this.closeExecutor = requireNonNull(closeExecutor);
    this.idleTimeoutSupplier = requireNonNull(idleTimeoutSupplier);
    this.connection = requireNonNull(connection);
  }

  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    return connection.execute(command);
  }

  MachineShellClientFile file(String path) throws MachineShellClientException {
    return connection.file(path);
  }

  void closeNow() {
    try {
      connection.close();
    } catch (Exception e) {
      logger.log(WARNING, "Error occurred while closing connection", e);
    }
  }

  MachineShellClientConnectionBridgeCloseTask closeIdleTimeout(Callable<Void> beforeCloseAction) {
    return new MachineShellClientConnectionBridgeCloseTask(
        closeExecutor,
        idleTimeoutSupplier.get(),
        connection,
        beforeCloseAction);
  }
}
