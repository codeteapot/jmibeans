package com.github.codeteapot.jmibeans.shell.client.pool;

@FunctionalInterface
interface PooledMachineShellClientConnectionAcquiredStateConstructor {

  PooledMachineShellClientConnectionAcquiredState construct(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username);
}
