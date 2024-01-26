package com.github.codeteapot.jmibeans.shell.client.pool;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

@FunctionalInterface
interface MachineShellClientConnectionBridgeFactoryConstructor {

  MachineShellClientConnectionBridgeFactory construct(
      ScheduledExecutorService closeExecutor,
      Supplier<Duration> idleTimeoutSupplier);
}
