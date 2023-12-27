package com.github.codeteapot.jmibeans.event;

import java.util.EventListener;

/**
 * Listener of events produced on the platform.
 */
public interface PlatformListener extends EventListener {

  /**
   * Method called when a machine available event has occurred.
   *
   * <p>At this time, the referenced machine is available in the context of the platform. 
   * <pre>
   * assert context.lookup(event.getMachineRef()).isPresent();
   * </pre>
   *
   * @implNote It is legitimate to store the available machine reference, as long as the machine is
   *           obtained each time with the <b>context.lookup(storedRef)</b> call.
   *
   * @param event The details of the event.
   */
  void machineAvailable(MachineAvailableEvent event);

  /**
   * Method called when a machine loss event occurs.
   *
   * <p>The referenced machine is no longer available.
   * <pre>
   * assert !context.lookup(event.getMachineRef()).isPresent();
   * </pre>
   *
   * @implNote This is the right time to throw out anything related to the lost machine reference.
   *
   * @param event The details of the event.
   */
  void machineLost(MachineLostEvent event);
}
