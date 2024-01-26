package com.github.codeteapot.jmibeans.platform.event;

import static java.util.Objects.requireNonNull;
import java.util.EventObject;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;

/**
 * Base class of machine events.
 *
 * <p>
 * Every machine event includes the machine reference in the platform context.
 *
 * @see MachineRef
 * @see PlatformContext
 */
public abstract class MachineEvent extends EventObject {

  private static final long serialVersionUID = 1L;

  /**
   * Machine reference in the platform context.
   */
  private final MachineRef machineRef;

  /**
   * The constructor must receive the source and reference of the machine.
   *
   * @param source The object in which the event initially occurred.
   * @param machineRef The reference of the machine for which the event occurred.
   */
  protected MachineEvent(Object source, MachineRef machineRef) {
    super(source);
    this.machineRef = requireNonNull(machineRef);
  }

  /**
   * Reference of the machine involved in the platform context.
   *
   * @return The machine reference.
   */
  public MachineRef getMachineRef() {
    return machineRef;
  }
}
