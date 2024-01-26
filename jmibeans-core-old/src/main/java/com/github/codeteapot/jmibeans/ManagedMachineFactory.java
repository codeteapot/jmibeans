package com.github.codeteapot.jmibeans;

interface ManagedMachineFactory {

  void build(MachineBuilder builder) throws MachineBuildingException, InterruptedException;

  ManagedMachine getMachine(MachineSessionPoolReleaser sessionPoolReleaser);
}
