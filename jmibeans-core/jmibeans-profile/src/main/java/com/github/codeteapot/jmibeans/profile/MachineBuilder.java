package com.github.codeteapot.jmibeans.profile;

public interface MachineBuilder {

  void build(MachineBuilderContext context) throws MachineBuildingException;
}
