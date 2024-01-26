package com.github.codeteapot.jmibeans.port.docker;

import static com.github.codeteapot.testing.InetAddressCreator.getByName;
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
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.logging.Handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.testing.logging.LoggerStub;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import wiremock.net.minidev.json.JSONArray;
import wiremock.net.minidev.json.JSONObject;

@ExtendWith(MockitoExtension.class)
@WireMockTest
@Tag("integration")
class DockerPlatformPortTest {

  private static final String ANY_ROLE = "any-role";

  private static final MachineProfileName ANY_PROFILE_NAME = new MachineProfileName("any-profile");

  private static final long EXECUTOR_TERMINATION_TIMEOUT_MILLIS = 1200L;

  private static final String TEST_GROUP = "test-group";
  private static final String TEST_GROUP_LABEL = "com.github.codeteapot.jmi.group=test-group";

  private static final Duration EVENTS_TIMEOUT = ofSeconds(8L);

  private static final String INITIAL_TIME_STR = "2017-08-08T20:28:29.06202363Z";
  private static final String INITIAL_TIME_UNIX_MILLIS = "1502224109";
  private static final String FIRST_ITERATION_TIME_UNIX_MILLIS = "1502224117";
  private static final String SECOND_ITERATION_TIME_UNIX_MILLIS = "1502224125";

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

  private static final RuntimeException SOME_RUNTIME_EXCEPTION = new RuntimeException();

  private ExecutorService executor;

  private LoggerStub portLoggerStub;
  private LoggerStub controllerLoggerStub;
  private LoggerStub monitorLoggerStub;

  @Mock
  private Handler portLoggerHandler;

  @Mock
  private Handler controllerLoggerHandler;

  @Mock
  private Handler monitorLoggerHandler;

  private TestActiveCondition activeCondition;

  @Mock
  private DockerProfileResolver profileResolver;

  private DockerPlatformPort port;

  @BeforeEach
  void setUp(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
    executor = newSingleThreadExecutor();
    portLoggerStub = loggerStubFor(DockerPlatformPort.class.getName(), portLoggerHandler);
    controllerLoggerStub = loggerStubFor(DockerController.class.getName(), controllerLoggerHandler);
    monitorLoggerStub = loggerStubFor(DockerMonitor.class.getName(), monitorLoggerHandler);

    stubFor(get(urlPathEqualTo("/info"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONObject()
                .appendField("SystemTime", INITIAL_TIME_STR)
                .toString())));

    activeCondition = new TestActiveCondition();
    port = new DockerPlatformPort(
        TEST_GROUP,
        new DockerTarget("localhost", wmRuntimeInfo.getHttpPort()),
        EVENTS_TIMEOUT,
        profileResolver,
        activeCondition);
  }

  @AfterEach
  void tearDown() throws Exception {
    activeCondition.finish();
    executor.awaitTermination(EXECUTOR_TERMINATION_TIMEOUT_MILLIS, MILLISECONDS);
    portLoggerStub.restore();
    controllerLoggerStub.restore();
    monitorLoggerStub.restore();
  }

  @Test
  void acceptRunningContainerWithRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
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
    when(profileResolver.fromRole(SOME_ROLE))
        .thenReturn(Optional.of(SOME_PROFILE_NAME));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> verify(someManager).accept(
        eq(SOME_MACHINE_ID),
        argThat(new MachineLinkMatcher()
            .withProfileName(SOME_PROFILE_NAME)
            .withAgent(new MachineAgentMatcher()
                .withNetworks(new CollectionMatcher<MachineNetwork>()
                    .withSize(1)
                    .withElement(new MachineNetworkMatcher()
                        .withName(SOME_NETWORK_NAME)
                        .withAddress(SOME_NETWORK_ADDRESS)))))));
  }

  @Test
  void acceptRunningContainerWithoutRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject())
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject()
                            .appendField(SOME_NETWORK_NAME_VALUE, new JSONObject()
                                .appendField("IPAddress", SOME_NETWORK_ADDRESS_VALUE)))))
                .toString())));
    when(profileResolver.getDefault())
        .thenReturn(SOME_PROFILE_NAME);

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> verify(someManager).accept(
        eq(SOME_MACHINE_ID),
        argThat(new MachineLinkMatcher()
            .withProfileName(SOME_PROFILE_NAME)
            .withAgent(new MachineAgentMatcher()
                .withNetworks(new CollectionMatcher<MachineNetwork>()
                    .withSize(1)
                    .withElement(new MachineNetworkMatcher()
                        .withName(SOME_NETWORK_NAME)
                        .withAddress(SOME_NETWORK_ADDRESS)))))));
  }

  @Test
  void acceptCreatedContainerAfterStartWithRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "created"))
                .toString())));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
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
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "start")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    when(profileResolver.fromRole(SOME_ROLE))
        .thenReturn(Optional.of(SOME_PROFILE_NAME));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });
    activeCondition.pollEvents(1);

    await().untilAsserted(() -> verify(someManager).accept(
        eq(SOME_MACHINE_ID),
        argThat(new MachineLinkMatcher()
            .withProfileName(SOME_PROFILE_NAME)
            .withAgent(new MachineAgentMatcher()
                .withNetworks(new CollectionMatcher<MachineNetwork>()
                    .withSize(1)
                    .withElement(new MachineNetworkMatcher()
                        .withName(SOME_NETWORK_NAME)
                        .withAddress(SOME_NETWORK_ADDRESS)))))));
  }

  @Test
  void acceptCreatedContainerAfterStartWithoutRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "created"))
                .toString())));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject())
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject()
                            .appendField(SOME_NETWORK_NAME_VALUE, new JSONObject()
                                .appendField("IPAddress", SOME_NETWORK_ADDRESS_VALUE)))))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "start")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    when(profileResolver.getDefault())
        .thenReturn(SOME_PROFILE_NAME);

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });
    activeCondition.pollEvents(1);

    await().untilAsserted(() -> verify(someManager).accept(
        eq(SOME_MACHINE_ID),
        argThat(new MachineLinkMatcher()
            .withProfileName(SOME_PROFILE_NAME)
            .withAgent(new MachineAgentMatcher()
                .withNetworks(new CollectionMatcher<MachineNetwork>()
                    .withSize(1)
                    .withElement(new MachineNetworkMatcher()
                        .withName(SOME_NETWORK_NAME)
                        .withAddress(SOME_NETWORK_ADDRESS)))))));
  }

  @Test
  void forgetAndAcceptStartedContainerAfterRestartWithRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject()
                        .appendField("com.github.codeteapot.jmi.role", ANY_ROLE))
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject())))
                .toString())));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
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
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "restart")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    when(profileResolver.fromRole(anyString()))
        .thenReturn(Optional.of(ANY_PROFILE_NAME));
    when(profileResolver.fromRole(SOME_ROLE))
        .thenReturn(Optional.of(SOME_PROFILE_NAME));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });
    activeCondition.pollEvents(1);

    await().untilAsserted(() -> {
      InOrder order = inOrder(someManager);
      order.verify(someManager).forget(SOME_MACHINE_ID);
      order.verify(someManager).accept(
          eq(SOME_MACHINE_ID),
          argThat(new MachineLinkMatcher()
              .withProfileName(SOME_PROFILE_NAME)
              .withAgent(new MachineAgentMatcher()
                  .withNetworks(new CollectionMatcher<MachineNetwork>()
                      .withSize(1)
                      .withElement(new MachineNetworkMatcher()
                          .withName(SOME_NETWORK_NAME)
                          .withAddress(SOME_NETWORK_ADDRESS))))));
    });
  }

  @Test
  void forgetAndAcceptStartedContainerAfterRestartWithoutRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject()
                        .appendField("com.github.codeteapot.jmi.role", ANY_ROLE))
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject())))
                .toString())));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject())
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject()
                            .appendField(SOME_NETWORK_NAME_VALUE, new JSONObject()
                                .appendField("IPAddress", SOME_NETWORK_ADDRESS_VALUE)))))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "restart")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    when(profileResolver.getDefault())
        .thenReturn(SOME_PROFILE_NAME);

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });
    activeCondition.pollEvents(1);

    await().untilAsserted(() -> {
      InOrder order = inOrder(someManager);
      order.verify(someManager).forget(SOME_MACHINE_ID);
      order.verify(someManager).accept(
          eq(SOME_MACHINE_ID),
          argThat(new MachineLinkMatcher()
              .withProfileName(SOME_PROFILE_NAME)
              .withAgent(new MachineAgentMatcher()
                  .withNetworks(new CollectionMatcher<MachineNetwork>()
                      .withSize(1)
                      .withElement(new MachineNetworkMatcher()
                          .withName(SOME_NETWORK_NAME)
                          .withAddress(SOME_NETWORK_ADDRESS))))));
    });
  }

  @Test
  void acceptContainerAfterCreateAndStartWithRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .toString())));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
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
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "create")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SECOND_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "start")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    when(profileResolver.fromRole(SOME_ROLE))
        .thenReturn(Optional.of(SOME_PROFILE_NAME));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });
    activeCondition.pollEvents(2);

    await().untilAsserted(() -> verify(someManager).accept(
        eq(SOME_MACHINE_ID),
        argThat(new MachineLinkMatcher()
            .withProfileName(SOME_PROFILE_NAME)
            .withAgent(new MachineAgentMatcher()
                .withNetworks(new CollectionMatcher<MachineNetwork>()
                    .withSize(1)
                    .withElement(new MachineNetworkMatcher()
                        .withName(SOME_NETWORK_NAME)
                        .withAddress(SOME_NETWORK_ADDRESS)))))));
  }

  @Test
  void acceptContainerAfterCreateAndStartWithoutRole(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .toString())));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject())
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject()
                            .appendField(SOME_NETWORK_NAME_VALUE, new JSONObject()
                                .appendField("IPAddress", SOME_NETWORK_ADDRESS_VALUE)))))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "create")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(SECOND_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "start")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    when(profileResolver.getDefault())
        .thenReturn(SOME_PROFILE_NAME);

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });
    activeCondition.pollEvents(2);

    await().untilAsserted(() -> verify(someManager).accept(
        eq(SOME_MACHINE_ID),
        argThat(new MachineLinkMatcher()
            .withProfileName(SOME_PROFILE_NAME)
            .withAgent(new MachineAgentMatcher()
                .withNetworks(new CollectionMatcher<MachineNetwork>()
                    .withSize(1)
                    .withElement(new MachineNetworkMatcher()
                        .withName(SOME_NETWORK_NAME)
                        .withAddress(SOME_NETWORK_ADDRESS)))))));
  }

  @Test
  void forgetStartedContainerAfterStop(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject()
                        .appendField("com.github.codeteapot.jmi.role", ANY_ROLE))
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject())))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "stop")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    when(profileResolver.fromRole(anyString()))
        .thenReturn(Optional.of(ANY_PROFILE_NAME));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });
    activeCondition.pollEvents(1);

    await().untilAsserted(() -> verify(someManager).forget(SOME_MACHINE_ID));
  }

  @Test
  void logInfoWhenContainerIsDestroyed(@Mock MachineManager anyManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "created"))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "container")
                    .appendField("Action", "destroy")
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));

    executor.submit(() -> {
      port.listen(anyManager);
      return null;
    });
    activeCondition.pollEvents(1);

    await().untilAsserted(() -> verify(monitorLoggerHandler).publish(argThat(
        record -> record.getLevel().equals(INFO))));
  }

  @Test
  void connectNetworkOnRunningContainer(
      @Mock MachineManager someManager,
      @Mock PropertyChangeListener somePropertyChangeListener,
      @Mock PropertyChangeListener anotherPropertyChangeListener) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject()
                        .appendField("com.github.codeteapot.jmi.role", ANY_ROLE))
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject())))
                .toString())));
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("id", equalTo(SOME_CONTAINER_ID)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject())
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject()
                            .appendField(SOME_NETWORK_NAME_VALUE, new JSONObject()
                                .appendField("IPAddress", SOME_NETWORK_ADDRESS_VALUE)))))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "network")
                    .appendField("Action", "connect")
                    .appendField("Actor", new JSONObject()
                        .appendField("Attributes", new JSONObject()
                            .appendField("name", SOME_NETWORK_NAME_VALUE)))
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    when(profileResolver.fromRole(anyString()))
        .thenReturn(Optional.of(ANY_PROFILE_NAME));
    doAnswer(invocation -> {
      MachineAgent agent = invocation.getArgument(1, MachineLink.class).getAgent();
      agent.addPropertyChangeListener(somePropertyChangeListener);
      agent.addPropertyChangeListener(anotherPropertyChangeListener);
      agent.removePropertyChangeListener(anotherPropertyChangeListener);
      return null;
    }).when(someManager).accept(eq(SOME_MACHINE_ID), argThat(new MachineLinkMatcher()
        .withAgent(new MachineAgentMatcher()
            .withNetworks(new CollectionMatcher<MachineNetwork>()
                .withSize(0)))));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });
    activeCondition.pollEvents(1);

    await().untilAsserted(() -> {
      verify(somePropertyChangeListener).propertyChange(argThat(
          new PropertyChangeEventMatcher<Collection<MachineNetwork>>()
              .withPropertyName("networks")
              .withOldValue(new CollectionMatcher<MachineNetwork>()
                  .withSize(0))
              .withNewValue(new CollectionMatcher<MachineNetwork>()
                  .withSize(1)
                  .withElement(new MachineNetworkMatcher()
                      .withName(SOME_NETWORK_NAME)
                      .withAddress(SOME_NETWORK_ADDRESS)))));
      verify(anotherPropertyChangeListener, never()).propertyChange(any());
    });
  }

  @Test
  void disconnectNetworkFromRunningContainer(
      @Mock MachineManager someManager,
      @Mock PropertyChangeListener somePropertyChangeListener,
      @Mock PropertyChangeListener anotherPropertyChangeListener) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject()
                        .appendField("com.github.codeteapot.jmi.role", ANY_ROLE))
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject()
                            .appendField(SOME_NETWORK_NAME_VALUE, new JSONObject()
                                .appendField("IPAddress", SOME_NETWORK_ADDRESS_VALUE)))))
                .toString())));
    stubFor(get(urlPathEqualTo("/events"))
        .withQueryParam("filters", and(
            matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)),
            matchingJsonPath("type[0]", equalTo("container")),
            matchingJsonPath("type[1]", equalTo("network"))))
        .withQueryParam("since", equalTo(INITIAL_TIME_UNIX_MILLIS))
        .withQueryParam("until", equalTo(FIRST_ITERATION_TIME_UNIX_MILLIS))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new StringBuilder()
                .append(new JSONObject()
                    .appendField("Type", "network")
                    .appendField("Action", "disconnect")
                    .appendField("Actor", new JSONObject()
                        .appendField("Attributes", new JSONObject()
                            .appendField("name", SOME_NETWORK_NAME_VALUE)))
                    .appendField("id", SOME_CONTAINER_ID))
                .toString())));
    when(profileResolver.fromRole(anyString()))
        .thenReturn(Optional.of(ANY_PROFILE_NAME));
    doAnswer(invocation -> {
      MachineAgent agent = invocation.getArgument(1, MachineLink.class).getAgent();
      agent.addPropertyChangeListener(somePropertyChangeListener);
      agent.addPropertyChangeListener(anotherPropertyChangeListener);
      agent.removePropertyChangeListener(anotherPropertyChangeListener);
      return null;
    }).when(someManager).accept(eq(SOME_MACHINE_ID), argThat(new MachineLinkMatcher()
        .withAgent(new MachineAgentMatcher()
            .withNetworks(new CollectionMatcher<MachineNetwork>()
                .withSize(1)
                .withElement(new MachineNetworkMatcher()
                    .withName(SOME_NETWORK_NAME)
                    .withAddress(SOME_NETWORK_ADDRESS))))));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });
    activeCondition.pollEvents(1);

    await().untilAsserted(() -> {
      verify(somePropertyChangeListener).propertyChange(argThat(
          new PropertyChangeEventMatcher<Collection<MachineNetwork>>()
              .withPropertyName("networks")
              .withOldValue(new CollectionMatcher<MachineNetwork>()
                  .withSize(1)
                  .withElement(new MachineNetworkMatcher()
                      .withName(SOME_NETWORK_NAME)
                      .withAddress(SOME_NETWORK_ADDRESS)))
              .withNewValue(new CollectionMatcher<MachineNetwork>()
                  .withSize(0))));
      verify(anotherPropertyChangeListener, never()).propertyChange(any());
    });
  }

  @Test
  void logSevereWhenFinishedWithError(@Mock MachineManager someManager) {
    stubFor(get(urlPathEqualTo("/containers/json"))
        .withQueryParam("filters", matchingJsonPath("label[0]", equalTo(TEST_GROUP_LABEL)))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody(new JSONArray()
                .appendElement(new JSONObject()
                    .appendField("Id", SOME_CONTAINER_ID)
                    .appendField("Status", "running")
                    .appendField("Labels", new JSONObject()
                        .appendField("com.github.codeteapot.jmi.role", ANY_ROLE))
                    .appendField("NetworkSettings", new JSONObject()
                        .appendField("Networks", new JSONObject())))
                .toString())));
    when(profileResolver.fromRole(anyString()))
        .thenReturn(Optional.of(ANY_PROFILE_NAME));
    doThrow(SOME_RUNTIME_EXCEPTION)
        .when(someManager).accept(eq(SOME_MACHINE_ID), argThat(new MachineLinkMatcher()
            .withAgent(new MachineAgentMatcher()
                .withNetworks(new CollectionMatcher<MachineNetwork>()
                    .withSize(0)))));

    executor.submit(() -> {
      port.listen(someManager);
      return null;
    });

    await().untilAsserted(() -> verify(portLoggerHandler).publish(argThat(
        record -> record.getLevel().equals(SEVERE) &&
            record.getThrown().equals(SOME_RUNTIME_EXCEPTION))));
  }
}
