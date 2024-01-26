package com.github.codeteapot.jmibeans.profile;

@FunctionalInterface
public interface MachineBuilder {

  // TODO DOC Return value must be not null
  MachineBuilderResult build(MachineBuilderContext context)
      throws MachineBuildingException, InterruptedException;
}
