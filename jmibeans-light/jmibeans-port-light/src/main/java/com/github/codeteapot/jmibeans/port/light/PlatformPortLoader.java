package com.github.codeteapot.jmibeans.port.light;

import static java.util.Objects.requireNonNull;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.port.PlatformPortFactory;

class PlatformPortLoader {

  private PlatformPortLoaderState state;

  PlatformPortLoader(String type, Map<String, Object> properties) {
    state = initial(newState -> state = requireNonNull(newState), type, properties);
  }

  PlatformPort load(ServiceLoader<PlatformPortFactory> serviceLoader) {
    return state.load(serviceLoader);
  }

  private static PlatformPortLoaderState initial(
      Consumer<PlatformPortLoaderState> changeStateAction,
      String type,
      Map<String, Object> properties) {
    return new PlatformPortLoaderInitialState(
        new PlatformPortLoaderStateChanger(changeStateAction),
        type,
        properties);
  }
}
