package com.github.codeteapot.jmibeans.port.docker;

import static com.github.codeteapot.jmibeans.port.docker.DockerLabels.groupFilter;
import static com.github.dockerjava.api.model.EventType.CONTAINER;
import static com.github.dockerjava.api.model.EventType.NETWORK;
import static com.github.dockerjava.core.DefaultDockerClientConfig.createDefaultConfigBuilder;
import static com.github.dockerjava.core.DockerClientImpl.getInstance;
import static java.lang.String.valueOf;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Collections.singleton;
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
import java.util.logging.Logger;

// TODO DESIGN Implementation MBean for exposing monitors
public class DockerPlatformPort implements PlatformPort {

  private static final Duration DEFAULT_EVENTS_TIMEOUT = ofSeconds(20L);
  private static final Duration RESPONSE_TIMEOUT_PADDING = ofMillis(100L);

  private static final Logger logger = getLogger(DockerPlatformPort.class.getName());

  private final String group;
  private final DockerTarget target;
  private final Duration eventsTimeout;
  private final DockerProfileResolver profileResolver;
  private final ActiveCondition activeCondition;

  public DockerPlatformPort(
      String group,
      DockerTarget target,
      Duration eventsTimeout,
      DockerProfileResolver profileResolver) {
    this(group, target, eventsTimeout, profileResolver, () -> true);
  }

  DockerPlatformPort(
      String group,
      DockerTarget target,
      Duration eventsTimeout,
      DockerProfileResolver profileResolver,
      ActiveCondition activeCondition) {
    this.group = requireNonNull(group);
    this.target = target;
    this.eventsTimeout = ofNullable(eventsTimeout).orElse(DEFAULT_EVENTS_TIMEOUT);
    this.profileResolver = requireNonNull(profileResolver);
    this.activeCondition = requireNonNull(activeCondition);
  }

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
      Map<String, String> labelFilter = groupFilter(group);
      DockerController controller = new DockerController(
          manager,
          profileResolver,
          containerId -> {
            return client.listContainersCmd()
                .withIdFilter(singleton(containerId))
                .withLabelFilter(labelFilter) // TODO Group filtering is not needed with ID
                .exec()
                .stream()
                .findAny();
          });
      Instant sinceTime = fromInfoTimestamp(client.infoCmd()
          .exec()
          .getSystemTime());
      controller.init(client.listContainersCmd()
          .withLabelFilter(labelFilter)
          .exec());
      while (activeCondition.test()) {
        Instant untilTime = sinceTime.plus(eventsTimeout);
        logger.fine(new StringBuilder()
            .append("Processing events from ")
            .append(sinceTime).append(" until ").append(untilTime)
            .toString());
        AwaitCompletionFix fix = new AwaitCompletionFix();
        client.eventsCmd()
            .withEventTypeFilter(CONTAINER, NETWORK)
            .withLabelFilter(labelFilter)
            .withSince(toEventTimestamp(sinceTime))
            .withUntil(toEventTimestamp(untilTime))
            .exec(controller)
            .awaitCompletion();
        fix.apply(eventsTimeout);
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
