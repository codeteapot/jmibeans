package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class ManagedMachineBuildingJob {

  private final ExecutorService builderExecutor;
  private final MachineBuilder builder;

  ManagedMachineBuildingJob(ExecutorService builderExecutor, MachineBuilder builder) {
    this.builderExecutor = requireNonNull(builderExecutor);
    this.builder = requireNonNull(builder);
  }

  Future<Void> submit(ManagedMachineBuildingJobAction action) {
    return builderExecutor.submit(() -> { // TODO Handle RejectedExecutionException
      action.build(builder);
      return null;
    });
  }
}
