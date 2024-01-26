package com.github.codeteapot.jmibeans.port.docker;

import static java.lang.Character.digit;
import static java.lang.String.format;
import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.port.MachineId;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import java.util.List;
import java.util.logging.Logger;

class DockerPlatformPortForwarder {

  private static final Logger logger = getLogger(DockerPlatformPortForwarder.class.getName());

  private final DockerClient client;
  private final MachineManager manager;
  private final DockerProfileResolver profileResolver;

  DockerPlatformPortForwarder(
      MachineManager manager,
      DockerClient client,
      DockerProfileResolver profileResolver) {
    this.manager = requireNonNull(manager);
    this.client = requireNonNull(client);
    this.profileResolver = requireNonNull(profileResolver);
  }

  void accept(Container container) {
    try {
      manager.accept(toMachineId(container.getId()), new DockerMachineLink(
          profileResolver,
          container));
    } catch (IllegalArgumentException e) {
      logger.log(SEVERE, "Machine accept failure", e);
    }
  }

  void accept(String containerId) {
    List<Container> containers = client.listContainersCmd()
        .withIdFilter(singleton(containerId))
        .exec();
    if (containers.isEmpty()) {
      logger.warning(format("Unable to take container %s", containerId));
    } else {
      accept(containers.get(0));
    }
  }

  void forget(String containerId) {
    manager.forget(toMachineId(containerId));
  }

  private static MachineId toMachineId(String containerId) {
    return new MachineId(fromHex(containerId));
  }

  private static byte[] fromHex(String str) {
    int len = str.length();
    byte[] bytes = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      int i1 = digit(str.charAt(i), 16) << 4;
      int i2 = digit(str.charAt(i + 1), 16);
      bytes[i / 2] = (byte) (i1 + i2);
    }
    return bytes;
  }
}
