package com.github.codeteapot.jmibeans.machine;

import java.beans.PropertyChangeListener;
import java.util.Set;

public interface MachineAgent {

  Set<MachineNetwork> getNetworks();

  void addPropertyChangeListener(PropertyChangeListener listener);

  void removePropertyChangeListener(PropertyChangeListener listener);
}
