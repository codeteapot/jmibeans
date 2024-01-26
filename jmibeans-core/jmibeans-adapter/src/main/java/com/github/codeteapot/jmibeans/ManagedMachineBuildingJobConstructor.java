package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import java.util.concurrent.ExecutorService;

@FunctionalInterface
interface ManagedMachineBuildingJobConstructor {

  ManagedMachineBuildingJob construct(ExecutorService builderExecutor, MachineBuilder builder);
}
