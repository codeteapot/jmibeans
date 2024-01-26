package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Collections.singletonMap;
import static java.util.Optional.ofNullable;

import com.github.dockerjava.api.model.Container;
import java.util.Map;
import java.util.Optional;

class DockerLabels {

  private static final String DOCKER_PORT_LABEL_PREFIX = "com.github.codeteapot.jmi.";
  private static final String GROUP_LABEL_NAME = "group";
  private static final String ROLE_LABEL_NAME = "role";

  private DockerLabels() {}

  static Map<String, String> groupFilter(String group) {
    return singletonMap(DOCKER_PORT_LABEL_PREFIX.concat(GROUP_LABEL_NAME), group);
  }

  static Optional<String> getRoleLabelValue(Container container) {
    return ofNullable(container.getLabels().get(DOCKER_PORT_LABEL_PREFIX.concat(ROLE_LABEL_NAME)));
  }
}
