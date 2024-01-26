package com.github.codeteapot.jmibeans.shell.client.pool;

import java.util.Optional;
import java.util.function.Supplier;

@FunctionalInterface
interface PooledMachineShellClientConnectionErrorOccurredStateConstructor {

  PooledMachineShellClientConnectionErrorOccurredState construct(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      Supplier<Optional<Exception>> exceptionSupplier);
}
