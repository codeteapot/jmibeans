package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

public class DockerTarget {

  private final String host;
  private final int port;

  public DockerTarget(String host, int port) {
    this.host = requireNonNull(host);
    this.port = port;
  }

  String toDockerHost() {
    return String.format("tcp://%s:%d", host, port);
  }
}
