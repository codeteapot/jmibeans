package com.github.codeteapot.jmibeans.profile;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import java.util.Set;

public interface MachineBuilderContext {

  Set<String> getProperty(String name);

  MachineAgent getAgent();

  // TODO DESIGN Rename it
  void addDisposeAction(MachineDisposeAction action);
}
