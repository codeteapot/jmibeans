package com.github.codeteapot.jmibeans.profile;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import java.util.Set;

public interface MachineBuilderContext {

  // TODO DESIGN Move to Set<Object>
  Set<String> getProperty(String name);

  MachineAgent getAgent();

  void addDisposeAction(MachineDisposeAction action);
}
