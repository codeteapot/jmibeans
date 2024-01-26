package com.github.codeteapot.jmibeans.port.docker;

import java.util.Optional;

import com.github.dockerjava.api.model.Container;

@FunctionalInterface
interface DockerContainerMapper {

  Optional<Container> map(String containerId);
}
