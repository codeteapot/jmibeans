package com.github.codeteapot.jmibeans.shell.client.pool;

import java.util.function.Consumer;

@FunctionalInterface
interface PooledMachineShellClientConnectionConstructor {

  PooledMachineShellClientConnection construct(
      MachineShellClientConnectionBridge bridge,
      String username,
      Consumer<PooledMachineShellClientConnection> removeAction);
}
