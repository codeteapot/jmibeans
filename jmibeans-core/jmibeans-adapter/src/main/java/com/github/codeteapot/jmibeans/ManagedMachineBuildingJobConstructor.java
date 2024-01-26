package com.github.codeteapot.jmibeans;

import java.util.concurrent.ExecutorService;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperties;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

@FunctionalInterface
interface ManagedMachineBuildingJobConstructor {

  ManagedMachineBuildingJob construct(
      MachineContainerBuilderPropertyConverters builderPropertyConverters,
      ExecutorService builderExecutor,
      MachineBuilderProperties builderProperties,
      MachineBuilder builder);
}
