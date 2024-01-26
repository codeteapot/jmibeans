package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;

public interface PlatformEventTarget {

  void fireAvailableEvent(MachineAvailableEvent event);

  void fireLostEvent(MachineLostEvent event);
}
