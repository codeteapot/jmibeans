package com.github.codeteapot.jmibeans.shell.client.pool;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;

@FunctionalInterface
interface MachineShellClientConnectionBridgeConstructor {

  MachineShellClientConnectionBridge construct(
      ScheduledExecutorService closeExecutor,
      Supplier<Duration> idleTimeout,
      MachineShellClientConnection connection);
}
