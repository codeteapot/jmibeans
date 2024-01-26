package com.github.codeteapot.jmibeans.shell.client.pool;

@FunctionalInterface
interface MachineShellConnectionStateMapper {

  MachineShellConnectionState map(MachineShellConnectionStateChanger stateChanger);
}
