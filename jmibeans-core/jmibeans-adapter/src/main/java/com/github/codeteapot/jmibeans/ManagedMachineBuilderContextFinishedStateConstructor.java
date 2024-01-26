package com.github.codeteapot.jmibeans;

import java.util.Set;

@FunctionalInterface
interface ManagedMachineBuilderContextFinishedStateConstructor {

  ManagedMachineBuilderContextFinishedState construct(
      ManagedMachineBuilderContextStateChanger stateChanger,
      Set<Runnable> disposeTasks);
}
