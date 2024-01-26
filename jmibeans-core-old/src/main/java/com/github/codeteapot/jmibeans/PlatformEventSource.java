package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.event.PlatformListener;

/**
 * Source through which events produced by the platform adapter are fired.
 *
 * @see PlatformAdapter
 * @see PlatformListener
 */
public interface PlatformEventSource {

  /**
   * Triggers an event associated with the fact that a machine becomes available.
   *
   * @param event The available machine event object.
   */
  void fireEvent(MachineAvailableEvent event);

  /**
   * Triggers an event associated with the loss of a machine.
   *
   * @param event The machine lost event object.
   */
  void fireEvent(MachineLostEvent event);
}
