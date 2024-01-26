package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;

/**
 * Object through which the platform events are fired.
 *
 * @see PlatformAdapter
 */
public interface PlatformEventTarget {

  /**
   * Fires the event that occurs when a machine becomes available.
   *
   * @param event The event to fire.
   */
  void fireAvailable(MachineAvailableEvent event);

  /**
   * Fires the event that occurs when a machine is lost.
   *
   * @param event The event to fire.
   */
  void fireLost(MachineLostEvent event);
}
