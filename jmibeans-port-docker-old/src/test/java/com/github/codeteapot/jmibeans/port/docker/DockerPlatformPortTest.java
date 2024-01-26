package com.github.codeteapot.jmibeans.port.docker;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.and;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.port.MachineId;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.MachineSessionHostResolutionException;
import com.github.codeteapot.jmibeans.port.docker.DockerEventsResultCallback;
import com.github.codeteapot.jmibeans.port.docker.DockerPlatformPort;
import com.github.codeteapot.jmibeans.port.docker.DockerPlatformPortForwarder;
import com.github.codeteapot.jmibeans.port.docker.DockerProfileResolver;
import com.github.codeteapot.jmibeans.port.docker.DockerTarget;
import com.github.codeteapot.testing.InetAddressCreator;
import com.github.codeteapot.testing.logging.LoggerStub;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wiremock.com.fasterxml.jackson.databind.node.JsonNodeFactory;
import wiremock.com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
@WireMockTest
@Tag("integration")
public class DockerPlatformPortTest {

  private static final long EXECUTOR_TERMINATION_TIMEOUT_MILLIS = 800L;
  private static final int EVENTS_RESPONSE_DELAY = 600;

  private static final String SOME_GROUP = "some-group";
  private static final String SOME_GROUP_LABEL = "com.github.codeteapot.jmi.group=some-group";

  private static final Duration SOME_EVENTS_TIMEOUT = ofSeconds(8L);

  private static final String SOME_CURRENT_TIME_STR = "2017-08-08T20:28:29.06202363Z";
  private static final String SOME_CURRENT_TIME_UNIX_MILLIS = "1502224109";
  private static final String SOME_SINCE_TIME_UNIX_MILLIS = "1502224117";
  private static final String SOME_UNTIL_TIME_UNIX_MILLIS = "1502224125";

  private static final String SOME_CONTAINER_ID = "704f";
  private static final MachineId SOME_MACHINE_ID = new MachineId(new byte[] {0x70, 0x4f});

  private static final String SOME_ROLE = "some-role";
  private static final MachineProfileName SOME_PROFILE_NAME = new MachineProfileName("some-name");

  private static final String SOME_IRRELEVANT_EVENT_ACTION = "create";

  private static final IllegalArgumentException SOME_ILLEGAL_ARGUMENT_EXCEPTION =
      new IllegalArgumentException();

  private static final MachineNetworkName SOME_NETWORK_NAME = new MachineNetworkName(
      "some-network");
  private static final String SOME_NETWORK_NAME_VALUE = "some-network";
  private static final InetAddress SOME_SESSION_HOST = InetAddressCreator.getByName("1.1.1.1");
  private static final String SOME_SESSION_HOST_NAME = "1.1.1.1";

  private static final MachineNetworkName ANOTHER_NETWORK_NAME = new MachineNetworkName(
      "another-network");
  private static final String ANOTHER_NETWORK_NAME_VALUE = "another-network";

  private static final String UNKNOWN_SESSION_HOST_NAME = "__unknown_host__";

  private JsonNodeFactory jsonNodeFactory;
  private ExecutorService executor;

  private LoggerStub portLoggerStub;
  private LoggerStub callbackLoggerStub;
  private LoggerStub forwarderLoggerStub;

  @Mock
  private Handler portLoggerHandler;

  @Mock
  private Handler callbackLoggerHandler;

  @Mock
  private Handler forwarderLoggerHandler;

  private AtomicBoolean active;

  @Mock
  private DockerProfileResolver profileResolver;

  private DockerPlatformPort port;

  @BeforeEach
  public void setUp(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
    jsonNodeFactory = JsonNodeFactory.instance;
    executor = newSingleThreadExecutor();

    portLoggerStub = loggerStubFor(DockerPlatformPort.class.getName(), portLoggerHandler);
    callbackLoggerStub = loggerStubFor(
        DockerEventsResultCallback.class.getName(),
        callbackLoggerHandler);
    forwarderLoggerStub = loggerStubFor(
        DockerPlatformPortForwarder.class.getName(),
        forwarderLoggerHandler);

    stubFor(get(urlPathEqualTo("/info"))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("SystemTime", SOME_CURRENT_TIME_STR))));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(SOME_CURRENT_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SOME_SINCE_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)));

    active = new AtomicBoolean(true);
    port = new DockerPlatformPort(
        SOME_GROUP,
        new DockerTarget("localhost", wmRuntimeInfo.getHttpPort()),
        SOME_EVENTS_TIMEOUT,
        active::get,
        profileResolver);
  }

  @AfterEach
  public void tearDown() throws Exception {
    active.set(false);
    executor.awaitTermination(EXECUTOR_TERMINATION_TIMEOUT_MILLIS, MILLISECONDS);
    portLoggerStub.restore();
    callbackLoggerStub.restore();
    forwarderLoggerStub.restore();
  }

  @Test
  public void acceptExistingMachineWithRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)
                    .<ObjectNode>set("Labels", jsonNodeFactory.objectNode()
                        .put("com.github.codeteapot.jmi.role", SOME_ROLE))
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(SOME_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", SOME_SESSION_HOST_NAME))))))));
    when(profileResolver.fromRole(SOME_ROLE))
        .thenReturn(Optional.of(SOME_PROFILE_NAME));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).accept(eq(SOME_MACHINE_ID), argThat(link -> {
        try {
          return link.getProfileName().equals(SOME_PROFILE_NAME)
              && link.getSessionHost(SOME_NETWORK_NAME).equals(SOME_SESSION_HOST);
        } catch (MachineSessionHostResolutionException e) {
          return false;
        }
      }));
    });
  }

  @Test
  public void acceptExistingMachineWithoutRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)
                    .<ObjectNode>set("Labels", jsonNodeFactory.objectNode())
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(SOME_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", SOME_SESSION_HOST_NAME))))))));
    when(profileResolver.getDefault())
        .thenReturn(SOME_PROFILE_NAME);

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).accept(eq(SOME_MACHINE_ID), argThat(link -> {
        try {
          return link.getProfileName().equals(SOME_PROFILE_NAME)
              && link.getSessionHost(SOME_NETWORK_NAME).equals(SOME_SESSION_HOST);
        } catch (MachineSessionHostResolutionException e) {
          return false;
        }
      }));
    });
  }

  @Test
  public void acceptStartedMachineWithRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(SOME_SINCE_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SOME_UNTIL_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("Action", "start")
                .put("id", SOME_CONTAINER_ID))));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)
                    .<ObjectNode>set("Labels", jsonNodeFactory.objectNode()
                        .put("com.github.codeteapot.jmi.role", SOME_ROLE))
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(SOME_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", SOME_SESSION_HOST_NAME))))))));
    when(profileResolver.fromRole(SOME_ROLE))
        .thenReturn(Optional.of(SOME_PROFILE_NAME));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).accept(eq(SOME_MACHINE_ID), argThat(link -> {
        try {
          return link.getProfileName().equals(SOME_PROFILE_NAME)
              && link.getSessionHost(SOME_NETWORK_NAME).equals(SOME_SESSION_HOST);
        } catch (MachineSessionHostResolutionException e) {
          return false;
        }
      }));
    });
  }

  @Test
  public void acceptStartedMachineWithoutRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(SOME_SINCE_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SOME_UNTIL_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("Action", "start")
                .put("id", SOME_CONTAINER_ID))));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)
                    .<ObjectNode>set("Labels", jsonNodeFactory.objectNode())
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(SOME_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", SOME_SESSION_HOST_NAME))))))));
    when(profileResolver.getDefault())
        .thenReturn(SOME_PROFILE_NAME);

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).accept(eq(SOME_MACHINE_ID), argThat(link -> {
        try {
          return link.getProfileName().equals(SOME_PROFILE_NAME)
              && link.getSessionHost(SOME_NETWORK_NAME).equals(SOME_SESSION_HOST);
        } catch (MachineSessionHostResolutionException e) {
          return false;
        }
      }));
    });
  }

  @Test
  public void forgetDiedMachine(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(SOME_SINCE_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SOME_UNTIL_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("Action", "die")
                .put("id", SOME_CONTAINER_ID))));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).forget(SOME_MACHINE_ID);
    });
  }

  @Test
  public void ignoreIrrelevantEvents(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(SOME_SINCE_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SOME_UNTIL_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("Action", SOME_IRRELEVANT_EVENT_ACTION))));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(callbackLoggerHandler).publish(argThat(record -> record.getLevel().equals(FINE)
          && record.getMessage().contains(SOME_IRRELEVANT_EVENT_ACTION)));
      verifyNoInteractions(someManager);
    });
  }

  @Test
  public void failWhenTryingToListen(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(500)));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(portLoggerHandler)
          .publish(argThat(record -> record.getLevel().equals(SEVERE)));
      verifyNoInteractions(someManager);
    });
  }

  @Test
  public void failWhenAcceptingExistingMachineWithIllegalArgument(
      @Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)))));
    doThrow(SOME_ILLEGAL_ARGUMENT_EXCEPTION)
        .when(someManager).accept(eq(SOME_MACHINE_ID), any());

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(forwarderLoggerHandler).publish(argThat(record -> record.getLevel().equals(SEVERE)
          && record.getThrown().equals(SOME_ILLEGAL_ARGUMENT_EXCEPTION)));
    });
  }

  @Test
  public void failWhenAcceptingStartedMachineWithIllegalArgument(
      @Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(SOME_SINCE_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SOME_UNTIL_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("Action", "start")
                .put("id", SOME_CONTAINER_ID))));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)))));
    doThrow(SOME_ILLEGAL_ARGUMENT_EXCEPTION)
        .when(someManager).accept(eq(SOME_MACHINE_ID), any());

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(forwarderLoggerHandler).publish(argThat(record -> record.getLevel().equals(SEVERE)
          && record.getThrown().equals(SOME_ILLEGAL_ARGUMENT_EXCEPTION)));
    });
  }

  @Test
  public void failWhenAcceptingWhenContainerDoesNotActuallyExists(
      @Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(SOME_SINCE_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SOME_UNTIL_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("Action", "start")
                .put("id", SOME_CONTAINER_ID))));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode())));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(forwarderLoggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)
          && record.getMessage().contains(SOME_CONTAINER_ID)));
    });
  }

  @Test
  public void failWhenAcceptingExistingMachineWithUnknownNetwork(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(SOME_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", SOME_SESSION_HOST_NAME))))))));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).accept(eq(SOME_MACHINE_ID), argThat(link -> {
        try {
          link.getSessionHost(ANOTHER_NETWORK_NAME);
          return false;
        } catch (MachineSessionHostResolutionException e) {
          return e.getMessage().contains(ANOTHER_NETWORK_NAME_VALUE);
        }
      }));
    });
  }

  @Test
  public void failWhenAcceptingStartedMachineWithUnknownNetwork(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(SOME_SINCE_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SOME_UNTIL_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("Action", "start")
                .put("id", SOME_CONTAINER_ID))));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(SOME_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", SOME_SESSION_HOST_NAME))))))));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).accept(eq(SOME_MACHINE_ID), argThat(link -> {
        try {
          link.getSessionHost(ANOTHER_NETWORK_NAME);
          return false;
        } catch (MachineSessionHostResolutionException e) {
          return e.getMessage().contains(ANOTHER_NETWORK_NAME_VALUE);
        }
      }));
    });
  }

  @Test
  public void failWhenAcceptingExistingMachineWithUnknownHost(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(SOME_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", UNKNOWN_SESSION_HOST_NAME))))))));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).accept(eq(SOME_MACHINE_ID), argThat(link -> {
        try {
          link.getSessionHost(SOME_NETWORK_NAME);
          return false;
        } catch (MachineSessionHostResolutionException e) {
          return e.getCause() instanceof UnknownHostException;
        }
      }));
    });
  }

  @Test
  public void failWhenAcceptingStartedMachineWithUnknownHost(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(SOME_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(SOME_SINCE_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SOME_UNTIL_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("Action", "start")
                .put("id", SOME_CONTAINER_ID))));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", SOME_CONTAINER_ID)
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(SOME_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", UNKNOWN_SESSION_HOST_NAME))))))));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).accept(eq(SOME_MACHINE_ID), argThat(link -> {
        try {
          link.getSessionHost(SOME_NETWORK_NAME);
          return false;
        } catch (MachineSessionHostResolutionException e) {
          return e.getCause() instanceof UnknownHostException;
        }
      }));
    });
  }
}
