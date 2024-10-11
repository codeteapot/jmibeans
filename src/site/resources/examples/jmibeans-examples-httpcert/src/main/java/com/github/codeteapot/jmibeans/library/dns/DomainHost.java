package com.github.codeteapot.jmibeans.library.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.MachineRef;

class DomainHost {

  private final MachineRef machineRef;
  private final String name;

  DomainHost(MachineRef machineRef, String name) {
    this.machineRef = requireNonNull(machineRef);
    this.name = requireNonNull(name);
  }

  MachineRef getMachineRef() {
    return machineRef;
  }
  
  String getName() {
    return name;
  }
}
