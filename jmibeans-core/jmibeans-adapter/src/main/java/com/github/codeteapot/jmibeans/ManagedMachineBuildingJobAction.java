package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.profile.MachineBuilder;

@FunctionalInterface
interface ManagedMachineBuildingJobAction {

  void build(MachineBuilder builder) throws InterruptedException;
}
