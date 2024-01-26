package com.github.codeteapot.jmibeans.port.docker;

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
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.port.MachineId;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.MachineSessionHostResolutionException;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.port.docker.DockerPlatformPort;
import com.github.codeteapot.jmibeans.port.docker.DockerProfileResolver;
import com.github.codeteapot.jmibeans.port.docker.DockerTarget;
import com.github.codeteapot.testing.InetAddressCreator;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
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
public class DockerPlatformPortAcceptanceTest {

  private static final long TERMINATION_TIMEOUT_MILLIS = 2000L;
  private static final int EVENTS_RESPONSE_DELAY = 600;

  private static final Duration TEST_EVENTS_TIMEOUT = ofSeconds(8L);

  private static final String TEST_CURRENT_TIME_STR = "2017-08-08T20:28:29.06202363Z";
  private static final String TEST_CURRENT_TIME_UNIX_MILLIS = "1502224109";
  private static final String TEST_UNTIL_TIME_UNIX_MILLIS = "1502224117";

  private static final String TEST_GROUP = "test-group";
  private static final String TEST_GROUP_LABEL = "com.github.codeteapot.jmi.group=test-group";

  private static final String TEST_NETWORK_NAME_VALUE = "test-network";
  private static final MachineNetworkName TEST_NETWORK_NAME = new MachineNetworkName(
      "test-network");

  private static final String LB_ROLE = "lb";
  private static final String APP_ROLE = "app";

  private static final String HTTP_CONTAINER_ID = "7f01";
  private static final MachineId HTTP_MACHINE_ID = new MachineId(new byte[] {0x7f, 0x01});
  private static final MachineProfileName HTTP_PROFILE_NAME = new MachineProfileName("http");
  private static final InetAddress HTTP_SESSION_HOST = InetAddressCreator.getByName("1.1.1.2");
  private static final String HTTP_SESSION_HOST_NAME = "1.1.1.2";

  private static final String JAVA_CONTAINER_ID = "7f02";
  private static final MachineId JAVA_MACHINE_ID = new MachineId(new byte[] {0x7f, 0x02});
  private static final MachineProfileName JAVA_PROFILE_NAME = new MachineProfileName("java");
  private static final InetAddress JAVA_SESSION_HOST = InetAddressCreator.getByName("1.1.1.3");
  private static final String JAVA_SESSION_HOST_NAME = "1.1.1.3";

  private JsonNodeFactory jsonNodeFactory;

  @BeforeEach
  public void setUp() {
    jsonNodeFactory = JsonNodeFactory.instance;
  }

  @Test
  public void forwardEventsToMachineManager(
      WireMockRuntimeInfo wmRuntimeInfo,
      @Mock DockerProfileResolver someProfileResolver,
      @Mock MachineManager someManager) throws Exception {
    ExecutorService executor = newSingleThreadExecutor();
    PlatformPort port = new DockerPlatformPort(
        TEST_GROUP,
        new DockerTarget("localhost", wmRuntimeInfo.getHttpPort()),
        TEST_EVENTS_TIMEOUT,
        someProfileResolver);

    stubFor(get(urlPathEqualTo("/info"))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.objectNode()
                .put("SystemTime", TEST_CURRENT_TIME_STR))));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", HTTP_CONTAINER_ID)
                    .<ObjectNode>set("Labels", jsonNodeFactory.objectNode()
                        .put("com.github.codeteapot.jmi.role", LB_ROLE))
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(TEST_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", HTTP_SESSION_HOST_NAME))))))));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(JAVA_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withJsonBody(jsonNodeFactory.arrayNode()
                .add(jsonNodeFactory.objectNode()
                    .put("Id", JAVA_CONTAINER_ID)
                    .<ObjectNode>set("Labels", jsonNodeFactory.objectNode()
                        .put("com.github.codeteapot.jmi.role", APP_ROLE))
                    .set("NetworkSettings", jsonNodeFactory.objectNode()
                        .set("Networks", jsonNodeFactory.objectNode()
                            .set(TEST_NETWORK_NAME_VALUE, jsonNodeFactory.objectNode()
                                .put("IPAddress", JAVA_SESSION_HOST_NAME))))))));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type", equalTo("container"))))
        .withQueryParam("since", equalTo(TEST_CURRENT_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(TEST_UNTIL_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(EVENTS_RESPONSE_DELAY)
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(jsonNodeFactory.objectNode()
                    .put("Action", "start")
                    .put("id", JAVA_CONTAINER_ID))
                .append(jsonNodeFactory.objectNode()
                    .put("Action", "die")
                    .put("id", HTTP_CONTAINER_ID))
                .toString())));
    when(someProfileResolver.fromRole(LB_ROLE))
        .thenReturn(Optional.of(HTTP_PROFILE_NAME));
    when(someProfileResolver.fromRole(APP_ROLE))
        .thenReturn(Optional.of(JAVA_PROFILE_NAME));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      verify(someManager).accept(eq(HTTP_MACHINE_ID), argThat(link -> {
        try {
          return link.getProfileName().equals(HTTP_PROFILE_NAME)
              && link.getSessionHost(TEST_NETWORK_NAME).equals(HTTP_SESSION_HOST);
        } catch (MachineSessionHostResolutionException e) {
          return false;
        }
      }));
      verify(someManager).accept(eq(JAVA_MACHINE_ID), argThat(link -> {
        try {
          return link.getProfileName().equals(JAVA_PROFILE_NAME)
              && link.getSessionHost(TEST_NETWORK_NAME).equals(JAVA_SESSION_HOST);
        } catch (MachineSessionHostResolutionException e) {
          return false;
        }
      }));
      //verify(someManager).forget(HTTP_MACHINE_ID);
    });

    executor.shutdownNow();
    executor.awaitTermination(TERMINATION_TIMEOUT_MILLIS, MILLISECONDS);
  }
}
