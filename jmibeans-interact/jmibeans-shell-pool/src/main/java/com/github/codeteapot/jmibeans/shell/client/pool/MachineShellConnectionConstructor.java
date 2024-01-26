package com.github.codeteapot.jmibeans.shell.client.pool;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;

@FunctionalInterface
interface MachineShellConnectionConstructor {

  MachineShellConnection construct(PooledMachineShellClientConnection pooled);
}
