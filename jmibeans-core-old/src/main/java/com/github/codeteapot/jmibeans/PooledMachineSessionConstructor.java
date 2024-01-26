package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.session.MachineSession;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@FunctionalInterface
interface PooledMachineSessionConstructor {

  PooledMachineSession construct(
      MachineSession managedSession,
      ScheduledExecutorService releaseExecutor,
      Consumer<PooledMachineSession> removeAction,
      String username,
      Duration idleTimeout);
}
