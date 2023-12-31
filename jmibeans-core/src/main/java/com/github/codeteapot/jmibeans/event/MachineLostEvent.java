package com.github.codeteapot.jmibeans.event;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import java.beans.ConstructorProperties;

/**
 * Event that occurs when a machine is lost.
 */
public class MachineLostEvent extends MachineEvent {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs an event generated by the specified source and with the reference of the involved
   * machine.
   *
   * @param source The object that generated the event.
   * @param machineRef The reference of the involved machine.
   */
  @ConstructorProperties({
      "machineRef"
  })
  public MachineLostEvent(Object source, MachineRef machineRef) {
    super(source, machineRef);
  }
}
