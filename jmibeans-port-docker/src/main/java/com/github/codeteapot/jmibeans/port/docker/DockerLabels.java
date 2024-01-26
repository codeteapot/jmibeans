package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

import com.github.dockerjava.api.model.Container;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

class DockerLabels {

  private static final String DOCKER_PORT_LABEL_PREFIX = "com.github.codeteapot.jmi.";

  private static final String GROUP_LABEL_NAME = DOCKER_PORT_LABEL_PREFIX.concat("group");
  private static final String ROLE_LABEL_NAME = DOCKER_PORT_LABEL_PREFIX.concat("role");
  private static final String BUILDER_LABEL_PREFIX = DOCKER_PORT_LABEL_PREFIX.concat("builder.");

  private DockerLabels() {}

  static Map<String, String> groupFilter(String group) {
    return singletonMap(GROUP_LABEL_NAME, group);
  }

  static Optional<String> getRoleLabelValue(Container container) {
    return ofNullable(container.getLabels().get(ROLE_LABEL_NAME));
  }

  static Map<String, Set<String>> getBuilderProps(Container container) {
    return container.getLabels()
        .entrySet()
        .stream()
        .filter(entry -> entry.getKey().startsWith(BUILDER_LABEL_PREFIX))
        .map(entry -> new SimpleEntry<>(
            entry.getKey().substring(BUILDER_LABEL_PREFIX.length()),
            singleton(entry.getValue())))
        .collect(toMap(Entry::getKey, Entry::getValue));
  }
}
