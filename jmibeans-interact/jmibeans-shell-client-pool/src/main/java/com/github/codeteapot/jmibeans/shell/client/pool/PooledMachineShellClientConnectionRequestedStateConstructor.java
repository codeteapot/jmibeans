package com.github.codeteapot.jmibeans.shell.client.pool;

@FunctionalInterface
interface PooledMachineShellClientConnectionRequestedStateConstructor {

  PooledMachineShellClientConnectionRequestedState construct(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username);
}
