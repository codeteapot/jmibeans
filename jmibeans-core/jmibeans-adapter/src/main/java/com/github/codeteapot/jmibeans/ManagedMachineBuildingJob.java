package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperties;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperty;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

class ManagedMachineBuildingJob {

  private final MachineContainerBuilderPropertyConverters builderPropertyConverters;
  private final ExecutorService builderExecutor;
  private final MachineBuilderProperties builderProperties;
  private final MachineBuilder builder;
  private final ManagedMachineBuilderPropertyValueConstructor builderPropertyValueConstructor;

  ManagedMachineBuildingJob(
      MachineContainerBuilderPropertyConverters builderPropertyConverters,
      ExecutorService builderExecutor,
      MachineBuilderProperties builderProperties,
      MachineBuilder builder) {
    this(
        builderPropertyConverters,
        builderExecutor,
        builderProperties,
        builder,
        ManagedMachineBuilderPropertyValue::new);
  }

  ManagedMachineBuildingJob(
      MachineContainerBuilderPropertyConverters builderPropertyConverters,
      ExecutorService builderExecutor,
      MachineBuilderProperties builderProperties,
      MachineBuilder builder,
      ManagedMachineBuilderPropertyValueConstructor builderPropertyValueConstructor) {
    this.builderPropertyConverters = requireNonNull(builderPropertyConverters);
    this.builderExecutor = requireNonNull(builderExecutor);
    this.builderProperties = requireNonNull(builderProperties);
    this.builder = requireNonNull(builder);
    this.builderPropertyValueConstructor = requireNonNull(builderPropertyValueConstructor);
  }

  Future<Void> submit(ManagedMachineBuildingJobAction action) {
    return builderExecutor.submit(() -> { // TODO Handle RejectedExecutionException
      for (MachineBuilderProperty property : builderProperties) {
        builder.setProperty(property.getName(), builderPropertyValueConstructor.construct(
            builderPropertyConverters,
            property.getRawValue()));
      }
      action.build(builder);
      return null;
    });
  }
}
