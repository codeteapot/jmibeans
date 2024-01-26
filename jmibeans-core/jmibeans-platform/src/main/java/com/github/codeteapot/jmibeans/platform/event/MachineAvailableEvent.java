package com.github.codeteapot.jmibeans.platform.event;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import java.beans.ConstructorProperties;

public class MachineAvailableEvent extends MachineEvent {

  private static final long serialVersionUID = 1L;

  @ConstructorProperties({
      "machineRef"
  })
  public MachineAvailableEvent(Object source, MachineRef machineRef) {
    super(source, machineRef);
  }
}
