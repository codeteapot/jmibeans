package com.github.codeteapot.jmibeans.shell.client.pool;

@FunctionalInterface
interface MachineShellConnectionClosedStateConstructor {

  MachineShellConnectionClosedState construct(MachineShellConnectionStateChanger stateChanger);
}
