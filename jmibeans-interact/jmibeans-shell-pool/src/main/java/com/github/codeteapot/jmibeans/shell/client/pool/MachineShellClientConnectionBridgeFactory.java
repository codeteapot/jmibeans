package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;

class MachineShellClientConnectionBridgeFactory {

  private final ScheduledExecutorService closeExecutor;
  private final Supplier<Duration> idleTimeoutSupplier;
  private final MachineShellClientConnectionBridgeConstructor bridgeConstructor;

  MachineShellClientConnectionBridgeFactory(
      ScheduledExecutorService closeExecutor,
      Supplier<Duration> idleTimeoutSupplier) {
    this(closeExecutor, idleTimeoutSupplier, MachineShellClientConnectionBridge::new);
  }

  MachineShellClientConnectionBridgeFactory(
      ScheduledExecutorService closeExecutor,
      Supplier<Duration> idleTimeoutSupplier,
      MachineShellClientConnectionBridgeConstructor bridgeConstructor) {
    this.closeExecutor = requireNonNull(closeExecutor);
    this.idleTimeoutSupplier = requireNonNull(idleTimeoutSupplier);
    this.bridgeConstructor = requireNonNull(bridgeConstructor);
  }

  MachineShellClientConnectionBridge getBridge(MachineShellClientConnection connection) {
    return bridgeConstructor.construct(closeExecutor, idleTimeoutSupplier, connection);
  }
}
