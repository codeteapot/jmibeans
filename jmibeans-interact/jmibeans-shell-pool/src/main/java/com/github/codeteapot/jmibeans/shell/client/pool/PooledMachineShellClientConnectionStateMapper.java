package com.github.codeteapot.jmibeans.shell.client.pool;

@FunctionalInterface
interface PooledMachineShellClientConnectionStateMapper {

  PooledMachineShellClientConnectionState map(
      PooledMachineShellClientConnectionStateChanger stateChanger);
}
