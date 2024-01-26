package com.github.codeteapot.jmibeans.platform.event;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import java.util.EventObject;

public abstract class MachineEvent extends EventObject {

  private static final long serialVersionUID = 1L;

  private final MachineRef machineRef;

  protected MachineEvent(Object source, MachineRef machineRef) {
    super(source);
    this.machineRef = requireNonNull(machineRef);
  }

  public MachineRef getMachineRef() {
    return machineRef;
  }
}
