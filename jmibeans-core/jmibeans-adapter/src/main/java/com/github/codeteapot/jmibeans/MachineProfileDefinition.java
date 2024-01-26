package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineProfile;

class MachineProfileDefinition implements MachineProfile {

  private final MachineBuilder builder;

  MachineProfileDefinition(MachineBuilder builder) {
    this.builder = requireNonNull(builder);
  }

  @Override
  public MachineBuilder getBuilder() {
    return builder;
  }
}
