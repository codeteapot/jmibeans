package com.github.codeteapot.jmibeans.machine;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

/**
 * Bean that represents a machine offered by an infrastructure provider.
 */
public interface MachineAgent {

  /**
   * Networks to which the machine is associated.
   *
   * <p>There should not be more than one network with the same name.
   *
   * <p>The value of this property may change. This is reflected by a {@link PropertyChangeEvent}
   * for the {@code networks} property.
   *
   * @return The current copy of the network set.
   *
   * @see MachineNetwork#getName()
   */
  Set<MachineNetwork> getNetworks();

  /**
   * Add a property change listener.
   *
   * @param listener The listener to add.
   */
  void addPropertyChangeListener(PropertyChangeListener listener);

  /**
   * Remove a property change listener.
   *
   * @param listener The listener to remove.
   */
  void removePropertyChangeListener(PropertyChangeListener listener);
}
