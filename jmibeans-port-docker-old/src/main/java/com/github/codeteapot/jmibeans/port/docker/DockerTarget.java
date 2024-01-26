package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

/**
 * Docker service target.
 *
 * @see DockerPlatformPort
 */
public class DockerTarget {

  private final String host;
  private final int port;
  
  /**
   * Target with the specified host and port.
   *
   * @param host Host of the target.
   * @param port Port of the target.
   */
  public DockerTarget(String host, int port) {
    this.host = requireNonNull(host);
    this.port = port;
  }
  
  String toDockerHost() {
    return String.format("tcp://%s:%d", host, port);
  }
}
