package com.github.codeteapot.jmibeans;

@FunctionalInterface
interface ManagedMachineStateMapper {

  ManagedMachineState map(ManagedMachineStateChanger stateChanger);
}
