package com.github.codeteapot.jmibeans.port.docker;

import static com.github.codeteapot.jmibeans.port.docker.WireMockHamcrest.wmEqualTo;
import static com.github.codeteapot.jmibeans.testing.port.hamcrest.SomeMachineAgentMatcher.someMachineAgent;
import static com.github.codeteapot.jmibeans.testing.port.hamcrest.SomeMachineLinkMatcher.someMachineLink;
import static com.github.codeteapot.jmibeans.testing.port.hamcrest.SomeMachineNetworkMatcher.someMachineNetwork;
import static com.github.codeteapot.testing.InetAddressCreator.getByName;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.and;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wiremock.net.minidev.json.JSONArray;
import wiremock.net.minidev.json.JSONObject;

@ExtendWith(MockitoExtension.class)
@WireMockTest
@Tag("integration")
class DockerPlatformPortAcceptanceTest {

  private static final long EXECUTOR_TERMINATION_TIMEOUT_MILLIS = 1200L;

  private static final String TEST_GROUP = "test-group";
  private static final String TEST_GROUP_LABEL = "com.github.codeteapot.jmi.group=test-group";

  private static final Duration EVENTS_TIMEOUT = ofSeconds(8L);

  private static final String INITIAL_TIME_STR = "2017-08-08T20:28:29.06202363Z";
  private static final String INITIAL_TIME_UNIX_MILLIS = "1502224109";
  private static final String FIRST_ITERATION_TIME_UNIX_MILLIS = "1502224117";

  private static final int REMAINING_EVENTS_DELAY_MILLIS = 30000;

  private static final String SOME_CONTAINER_ID = "704f";
  private static final byte[] SOME_MACHINE_ID = {0x70, 0x4f};

  private static final String SOME_ROLE = "some-role";
  private static final MachineProfileName SOME_PROFILE_NAME =
      new MachineProfileName("some-profile");

  private static final MachineNetworkName SOME_NETWORK_NAME = new MachineNetworkName(
      "some-network");
  private static final String SOME_NETWORK_NAME_VALUE = "some-network";
  private static final InetAddress SOME_NETWORK_ADDRESS = getByName("1.1.1.1");
  private static final String SOME_NETWORK_ADDRESS_VALUE = "1.1.1.1";

  @Test
  void acceptAndForget(
      WireMockRuntimeInfo wmRuntimeInfo,
      @Mock DockerProfileResolver someProfileResolver,
      @Mock MachineManager someManager) throws Exception {
    stubFor(get(urlPathEqualTo("/info"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONObject()
                .appendField("SystemTime", INITIAL_TIME_STR)
                .toString())));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", wmEqualTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "created"))
                .toString())));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", wmEqualTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject()
                        .appendField("com.github.codeteapot.jmi.role", SOME_ROLE))
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject()
                            .appendField(SOME_NETWORK_NAME_VALUE, new JSONObject()
                                .appendField("IPAddress", SOME_NETWORK_ADDRESS_VALUE)))))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", wmEqualTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", wmEqualTo("container")),
            matchingJsonPath("type[1]", wmEqualTo("network"))))
        .withQueryParam("since", wmEqualTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", wmEqualTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            // TODO Put prints on the loop and see how they are repeated
            // .withFixedDelay(REMAINING_EVENTS_DELAY_MILLIS)
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "start")
                    .appendField("id", SOME_CONTAINER_ID))
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "stop")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", wmEqualTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", wmEqualTo("container")),
            matchingJsonPath("type[1]", wmEqualTo("network"))))
        .withQueryParam("since", wmEqualTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withFixedDelay(REMAINING_EVENTS_DELAY_MILLIS)
            .withStatus(200)));
    when(someProfileResolver.fromRole(SOME_ROLE))
        .thenReturn(Optional.of(SOME_PROFILE_NAME));

    DockerPlatformPort port = new DockerPlatformPort(
        TEST_GROUP,
        new DockerTarget("localhost", wmRuntimeInfo.getHttpPort()),
        EVENTS_TIMEOUT,
        someProfileResolver);
    ExecutorService executor = newSingleThreadExecutor();

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> {
      InOrder order = inOrder(someManager);
      order.verify(someManager).accept(eq(SOME_MACHINE_ID), argThat(someMachineLink()
          .withProfileName(equalTo(SOME_PROFILE_NAME))
          .withAgent(someMachineAgent()
              .withNetworks(contains(someMachineNetwork()
                  .withName(equalTo(SOME_NETWORK_NAME))
                  .withAddress(equalTo(SOME_NETWORK_ADDRESS)))))));
      order.verify(someManager).forget(SOME_MACHINE_ID);
    });

    executor.shutdown();
    executor.awaitTermination(EXECUTOR_TERMINATION_TIMEOUT_MILLIS, MILLISECONDS);
  }
}
