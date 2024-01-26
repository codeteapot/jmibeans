package com.github.codeteapot.jmibeans.profile;

import com.github.codeteapot.jmibeans.machine.MachineAgent;

public interface MachineBuilderContext {

  MachineAgent getAgent();

  void registerFacet(Object facet);
}
