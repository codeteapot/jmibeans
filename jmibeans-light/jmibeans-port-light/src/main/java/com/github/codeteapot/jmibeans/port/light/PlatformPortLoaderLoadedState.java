package com.github.codeteapot.jmibeans.port.light;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.port.PlatformPortFactory;
import com.github.codeteapot.jmibeans.port.PlatformPortFactoryException;

class PlatformPortLoaderLoadedState extends PlatformPortLoaderState {

  private static final Logger logger = getLogger(PlatformPortLoader.class.getName());

  private final PlatformPortFactory factory;

  PlatformPortLoaderLoadedState(
      PlatformPortLoaderStateChanger stateChanger,
      PlatformPortFactory factory) {
    super(stateChanger);
    this.factory = requireNonNull(factory);
  }

  @Override
  PlatformPort load(ServiceLoader<PlatformPortFactory> serviceLoader) {
    try {
      return factory.getPort();
    } catch (PlatformPortFactoryException | RuntimeException e) {
      logger.log(SEVERE, "Error loading port", e);
      return null;
    }
  }
}
