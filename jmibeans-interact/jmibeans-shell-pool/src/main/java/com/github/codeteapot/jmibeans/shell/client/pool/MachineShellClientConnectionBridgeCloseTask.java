package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

class MachineShellClientConnectionBridgeCloseTask {

  private final ScheduledFuture<Void> future;
  private final MachineShellClientConnection connection;
  private final Callable<Void> beforeCloseAction;

  MachineShellClientConnectionBridgeCloseTask(
      ScheduledExecutorService closeExecutor,
      Duration idleTimeout,
      MachineShellClientConnection connection,
      Callable<Void> beforeCloseAction) {
    // TODO Handle RejectedExecutionException
    future = closeExecutor.schedule(this::closeAction, idleTimeout.toMillis(), MILLISECONDS);
    this.connection = requireNonNull(connection);
    this.beforeCloseAction = requireNonNull(beforeCloseAction);
  }

  void cancel() {
    future.cancel(true);
  }

  private Void closeAction() throws Exception {
    beforeCloseAction.call();
    connection.close();
    return null;
  }
}
