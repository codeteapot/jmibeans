package com.github.codeteapot.jmibeans.platform.event;

import java.util.EventListener;

/**
 * Listener for all events that occur on the platform.
 */
public interface PlatformListener extends EventListener {

  /**
   * Handles available machine events.
   *
   * @param event The related event object.
   */
  void machineAvailable(MachineAvailableEvent event);

  /**
   * Handles lost machine events.
   *
   * @param event The related event object.
   */
  void machineLost(MachineLostEvent event);
}
