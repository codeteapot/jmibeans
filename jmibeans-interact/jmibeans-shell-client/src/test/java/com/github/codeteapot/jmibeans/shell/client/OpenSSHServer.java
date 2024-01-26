package com.github.codeteapot.jmibeans.shell.client;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.newOutputStream;
import static org.testcontainers.containers.Network.newNetwork;
import java.io.OutputStream;
import java.nio.file.Path;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

class OpenSSHServer {

  private static final String CONTAINER_IMAGE_NAME = "lscr.io/linuxserver/openssh-server:latest";

  private GenericContainer<?> container;
  private Network network;
  private Path dataDir;

  @SuppressWarnings("resource")
  public OpenSSHServer(
      Path tempDir,
      String username,
      char[] password,
      PublicKeySupplier publicKeySupplier) throws Exception {
    Path configDir = tempDir.resolve("config");
    dataDir = tempDir.resolve("data");
    container = new GenericContainer<>(DockerImageName.parse(CONTAINER_IMAGE_NAME))
        .withExposedPorts(2222) // Not the default port
        .withEnv("PUID", "1000")
        .withEnv("PGID", "1000")
        .withEnv("TZ", "Etc/UTC")
        .withEnv("PASSWORD_ACCESS", "true")
        .withEnv("PUBLIC_KEY_FILE", "/config/public-key.pem")
        .withEnv("USER_NAME", username)
        .withEnv("USER_PASSWORD", String.valueOf(password))
        .withFileSystemBind(configDir.toString(), "/config")
        .withFileSystemBind(dataDir.toString(), "/data");
    network = newNetwork();
    container.setNetwork(network);
    createDirectory(configDir);
    createDirectory(dataDir);
    configDir.resolve("public-key.pem");
    try (OutputStream output = newOutputStream(configDir.resolve("public-key.pem"))) {
      publicKeySupplier.get(output);
    }
  }

  String getHost() {
    return container.getHost();
  }

  int getPort() {
    return container.getFirstMappedPort();
  }

  void start() {
    container.start();
  }

  void stop() {
    container.stop();
  }

  void disconnect() {
    container.getDockerClient()
        .disconnectFromNetworkCmd()
        .withContainerId(container.getContainerId())
        .withNetworkId(network.getId())
        .exec();
  }

  void reconnect() {
    container.getDockerClient()
        .connectToNetworkCmd()
        .withContainerId(container.getContainerId())
        .withNetworkId(network.getId())
        .exec();
  }

  @FunctionalInterface
  interface PublicKeySupplier {

    void get(OutputStream output) throws Exception;
  }
}
