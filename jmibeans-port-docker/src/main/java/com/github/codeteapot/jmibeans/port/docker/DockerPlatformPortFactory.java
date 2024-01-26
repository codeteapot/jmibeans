package com.github.codeteapot.jmibeans.port.docker;

import static java.lang.Integer.parseInt;
import static java.util.Optional.ofNullable;
import java.time.Duration;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.port.PlatformPortFactory;
import com.github.codeteapot.jmibeans.port.PlatformPortFactoryException;

public class DockerPlatformPortFactory implements PlatformPortFactory {

  public static final String TYPE = "DOCKER";

  private String group;
  private String targetHost;
  private Integer targetPort;
  private Duration eventsTimeout;

  public DockerPlatformPortFactory() {
    group = null;
    targetHost = null;
    targetPort = null;
    eventsTimeout = null;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public PlatformPort getPort() throws PlatformPortFactoryException {
    return new DockerPlatformPort(
        ofNullable(group)
            .orElseThrow(() -> new PlatformPortFactoryException("Group is not defined")),
        ofNullable(targetHost)
            .flatMap(hostValue -> ofNullable(targetPort)
                .map(portValue -> new DockerTarget(hostValue, portValue)))
            .orElse(null),
        eventsTimeout);
  }

  @Override
  public void setProperty(String name, Object value) {
    switch (name) {
      case "group":
        group = value.toString();
        break;
      case "targetHost":
        targetHost = value.toString();
        break;
      case "targetPort":
        if (value instanceof Integer) {
          targetPort = (Integer) value;
        } else {
          targetPort = parseInt(value.toString());
        }
        break;
      case "eventsTimeout":
        if (value instanceof Duration) {
          eventsTimeout = (Duration) value;
        } else {
          eventsTimeout = Duration.parse(value.toString());
        }
        break;
    }
  }
}
