package com.github.codeteapot.jmibeans.port.docker;

import static com.github.codeteapot.jmibeans.port.docker.DockerLabels.groupFilter;
import static com.github.dockerjava.api.model.EventType.CONTAINER;
import static com.github.dockerjava.core.DefaultDockerClientConfig.createDefaultConfigBuilder;
import static com.github.dockerjava.core.DockerClientImpl.getInstance;
import static java.lang.String.valueOf;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Port for a Docker infrastructure.
 *
 * <p>In order not to intervene on containers that should not be taken into account by the port, the
 * containers involved must have the label {@code "com.github.codeteapot.jmi.group"} with the same
 * value of the {@code "group"} property of the port.
 */
public class DockerPlatformPort implements PlatformPort {

  private static final Duration DEFAULT_EVENTS_TIMEOUT = ofSeconds(20L);
  private static final Duration RESPONSE_TIMEOUT_PADDING = ofMillis(100L);

  private static final Logger logger = getLogger(DockerPlatformPort.class.getName());

  private final String group;
  private final DockerTarget target;
  private final Duration eventsTimeout;
  private final Supplier<Boolean> activeSupplier;
  private final DockerProfileResolver profileResolver;

  /**
   * Create the platform port for Docker.
   *
   * @param group Group of containers to take into account.
   * @param target Target where the Docker service is located.
   * @param eventsTimeout Time each request lasts when listening to events.
   * @param profileResolver Resolves the machine profile name of containers managed by the port.
   */
  public DockerPlatformPort(
      String group,
      DockerTarget target,
      Duration eventsTimeout,
      DockerProfileResolver profileResolver) {
    this(group, target, eventsTimeout, () -> true, profileResolver);
  }

  DockerPlatformPort(
      String group,
      DockerTarget target,
      Duration eventsTimeout,
      Supplier<Boolean> activeSupplier,
      DockerProfileResolver profileResolver) {
    this.group = requireNonNull(group);
    this.target = target;
    this.eventsTimeout = ofNullable(eventsTimeout).orElse(DEFAULT_EVENTS_TIMEOUT);
    this.activeSupplier = requireNonNull(activeSupplier);
    this.profileResolver = requireNonNull(profileResolver);
  }

  // TODO Be able to copy from offline link
  /**
   * Keeps the port aware of infrastructure changes.
   *
   * <p>This is a blocking operation. The listening process stays active indefinitely, until the
   * thread in which it is running is interrupted.
   *
   * @param manager Machine manager through which changes are notified to the platform.
   *
   * @throws InterruptedException When the thread in which the listener process is running is
   *         interrupted.
   */
  @Override
  public void listen(MachineManager manager) throws InterruptedException {
    try {
      logger.fine(new StringBuilder()
          .append("Listening Docker port; group: ").append(group)
          .toString());
      DefaultDockerClientConfig.Builder configBuilder = createDefaultConfigBuilder();
      ofNullable(target)
          .map(DockerTarget::toDockerHost)
          .ifPresent(configBuilder::withDockerHost);
      listen(manager, configBuilder.build());
    } catch (IOException | RuntimeException e) {
      logger.log(SEVERE, "Docker port finished with error", e);
    }
  }

  private void listen(MachineManager manager, DefaultDockerClientConfig config)
      throws IOException, InterruptedException {
    try (DockerClient client = getInstance(config, new ApacheDockerHttpClient.Builder()
        .dockerHost(config.getDockerHost())
        .sslConfig(config.getSSLConfig())
        .maxConnections(2) // SEVERE: Must be 2 at least (events + get container)
        .connectionTimeout(ofSeconds(20))
        .responseTimeout(eventsTimeout.plus(RESPONSE_TIMEOUT_PADDING))
        .build())) {
      DockerPlatformPortForwarder forwarder = new DockerPlatformPortForwarder(
          manager,
          client,
          profileResolver);
      Map<String, String> labels = groupFilter(group);
      client.listContainersCmd()
          .withLabelFilter(labels)
          .exec()
          .stream()
          .forEach(forwarder::accept);
      Instant sinceTime = fromInfoTimestamp(client.infoCmd()
          .exec()
          .getSystemTime());
      while (activeSupplier.get()) {
        Instant untilTime = sinceTime.plus(eventsTimeout);
        client.eventsCmd()
            .withEventTypeFilter(CONTAINER)
            .withLabelFilter(labels)
            .withSince(toEventTimestamp(sinceTime))
            .withUntil(toEventTimestamp(untilTime))
            .exec(new DockerEventsResultCallback(forwarder))
            .awaitCompletion();
        sinceTime = untilTime;
      }
    }
  }

  private static Instant fromInfoTimestamp(String timestamp) {
    return ISO_DATE_TIME.parse(timestamp, Instant::from);
  }

  private static String toEventTimestamp(Instant instant) {
    return valueOf(instant.toEpochMilli() / 1000L);
  }
}
