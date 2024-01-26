package com.github.codeteapot.jmibeans.shell.client.pool;

@FunctionalInterface
interface PooledMachineShellClientConnectionAvailableStateConstructor {

  PooledMachineShellClientConnectionAvailableState construct(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionBridge bridge,
      String username,
      MachineShellClientConnectionBridgeCloseTask closeTask);
}
