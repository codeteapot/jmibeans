package com.github.codeteapot.jmibeans.shell.client.pool;

import java.util.Optional;
import java.util.function.Supplier;

@FunctionalInterface
interface PooledMachineShellClientConnectionClosedStateConstructor {

  PooledMachineShellClientConnectionClosedState construct(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      Supplier<Optional<Exception>> exceptionSupplier);
}
