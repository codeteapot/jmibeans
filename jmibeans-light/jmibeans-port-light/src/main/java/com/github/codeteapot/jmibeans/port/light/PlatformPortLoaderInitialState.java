package com.github.codeteapot.jmibeans.port.light;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.port.PlatformPortFactory;
import com.github.codeteapot.jmibeans.port.PlatformPortFactoryException;

class PlatformPortLoaderInitialState extends PlatformPortLoaderState {

  private static final Logger logger = getLogger(PlatformPortLoader.class.getName());

  private final String type;
  private final Map<String, Object> properties;

  PlatformPortLoaderInitialState(
      PlatformPortLoaderStateChanger stateChanger,
      String type,
      Map<String, Object> properties) {
    super(stateChanger);
    this.type = requireNonNull(type);
    this.properties = requireNonNull(properties);
  }

  @Override
  PlatformPort load(ServiceLoader<PlatformPortFactory> serviceLoader) {
    return StreamSupport.stream(serviceLoader.spliterator(), false)
        .filter(service -> type.equals(service.getType()))
        .peek(this::setProperties)
        .findAny()
        .map(this::factoryLoaded)
        .orElseGet(this::factoryNotFound)
        .get();
  }

  private void setProperties(PlatformPortFactory factory) {
    properties.entrySet().forEach(entry -> {
      try {
        factory.setProperty(entry.getKey(), entry.getValue());
      } catch (PlatformPortFactoryException | RuntimeException e) {
        throw new PlatformPortsLoaderException(e);
      }
    });
  }

  private Supplier<PlatformPort> factoryLoaded(PlatformPortFactory factory) {
    return () -> {
      try {
        logger.info(new StringBuilder()
            .append("Loading port of type ").append(type)
            .toString());
        return factory.getPort();
      } catch (PlatformPortFactoryException | RuntimeException e) {
        throw new PlatformPortsLoaderException(e);
      } finally {
        stateChanger.loaded(factory);
      }
    };
  }

  private Supplier<PlatformPort> factoryNotFound() {
    return () -> {
      throw new PlatformPortsLoaderException(new StringBuilder()
          .append("Service for platform port loader of type ").append(type).append(" ")
          .append("was not found")
          .toString());
    };
  }
}
