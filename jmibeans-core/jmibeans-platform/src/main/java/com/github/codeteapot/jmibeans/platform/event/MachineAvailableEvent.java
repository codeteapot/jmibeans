package com.github.codeteapot.jmibeans.platform.event;

import java.beans.ConstructorProperties;
import com.github.codeteapot.jmibeans.platform.MachineRef;

/**
 * Event that occurs when a machine becomes available.
 *
 * @see PlatformListener#machineAvailable(MachineAvailableEvent)
 */
public class MachineAvailableEvent extends MachineEvent {

  private static final long serialVersionUID = 1L;

  /**
   * Construct the event given the source and machine reference.
   *
   * @param source The object in which the event initially occurred.
   * @param machineRef The reference of the machine for which the event occurred.
   */
  @ConstructorProperties({
      "machineRef"
  })
  public MachineAvailableEvent(Object source, MachineRef machineRef) {
    super(source, machineRef);
  }
}
