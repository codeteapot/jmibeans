package com.github.codeteapot.jmibeans.shell.client.pool;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;

@FunctionalInterface
interface PooledMachineShellClientConnectionAcquire {

  PooledMachineShellClientConnection acquire(String username) throws MachineShellClientException;
}
