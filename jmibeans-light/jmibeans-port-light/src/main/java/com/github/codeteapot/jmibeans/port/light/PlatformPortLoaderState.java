package com.github.codeteapot.jmibeans.port.light;

import static java.util.Objects.requireNonNull;
import java.util.ServiceLoader;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.port.PlatformPortFactory;

abstract class PlatformPortLoaderState {

  protected final PlatformPortLoaderStateChanger stateChanger;

  protected PlatformPortLoaderState(PlatformPortLoaderStateChanger stateChanger) {
    this.stateChanger = requireNonNull(stateChanger);
  }

  abstract PlatformPort load(ServiceLoader<PlatformPortFactory> serviceLoader);
}
