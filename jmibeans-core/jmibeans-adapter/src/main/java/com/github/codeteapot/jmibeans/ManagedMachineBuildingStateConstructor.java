package com.github.codeteapot.jmibeans;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

@FunctionalInterface
interface ManagedMachineBuildingStateConstructor {

  ManagedMachineBuildingState construct(
      ManagedMachineStateChanger stateChanger,
      MachineRef ref,
      Consumer<MachineRef> removalAction,
      PlatformEventTarget eventTarget,
      ExecutorService builderExecutor,
      MachineBuilder builder,
      MachineAgent agent);
}
