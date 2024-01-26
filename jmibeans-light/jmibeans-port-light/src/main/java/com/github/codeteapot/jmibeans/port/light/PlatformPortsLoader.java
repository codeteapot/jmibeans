package com.github.codeteapot.jmibeans.port.light;

import static java.util.Objects.requireNonNull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.port.PlatformPortFactory;

public final class PlatformPortsLoader implements Iterable<PlatformPort> {

  private final ServiceLoader<PlatformPortFactory> serviceLoader;
  private final Set<PlatformPortLoader> loaders;

  public PlatformPortsLoader(
      ServiceLoader<PlatformPortFactory> serviceLoader,
      PlatformPortsLoaderProperties properties) {
    this.serviceLoader = requireNonNull(serviceLoader);
    loaders = new HashSet<>(properties.loaders());
  }

  /**
   * TODO DOCUMENTATION ...
   *
   * @see ServiceLoader#iterator()
   */
  @Override
  public Iterator<PlatformPort> iterator() {
    return loaders.stream()
        .map(loader -> loader.load(serviceLoader))
        .iterator();
  }
}
