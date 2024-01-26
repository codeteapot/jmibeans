package com.github.codeteapot.jmibeans.port.light;

import static java.util.Objects.requireNonNull;
import java.util.Map;

class PlatformPortLoaderProperties {

  private final String type;
  private final Map<String, Object> properties;

  PlatformPortLoaderProperties(String type, Map<String, Object> properties) {
    this.type = requireNonNull(type);
    this.properties = requireNonNull(properties);
  }

  PlatformPortLoader loader() {
    return new PlatformPortLoader(type, properties);
  }

  void putProperty(String name, Object value) {
    properties.put(name, value);
  }
}
