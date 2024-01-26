package com.github.codeteapot.jmibeans.shell.client;

@FunctionalInterface
interface MachineShellClientConnectionStateMapper {

  JschMachineShellClientConnectionAvailableState map(
      MachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionEventSource eventSource);
}
