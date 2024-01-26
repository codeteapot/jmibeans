package com.github.codeteapot.jmibeans.shell.client.pool;

@FunctionalInterface
interface PooledMachineShellClientConnectionClosingStateConstructor {

  PooledMachineShellClientConnectionClosingState construct(
      PooledMachineShellClientConnectionStateChanger stateChanger);
}
