package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.jmibeans.Machine.facetGet;
import static com.github.codeteapot.testing.InetAddressCreator.getLocalHost;
import static java.lang.Thread.currentThread;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.MachineBuilder;
import com.github.codeteapot.jmibeans.MachineBuilderContext;
import com.github.codeteapot.jmibeans.MachineCatalog;
import com.github.codeteapot.jmibeans.MachineProfile;
import com.github.codeteapot.jmibeans.MachineRealm;
import com.github.codeteapot.jmibeans.MachineSessionPool;
import com.github.codeteapot.jmibeans.PlatformAdapter;
import com.github.codeteapot.jmibeans.PlatformEventSource;
import com.github.codeteapot.jmibeans.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.port.MachineId;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.session.MachineSession;
import com.github.codeteapot.jmibeans.session.MachineSessionAuthentication;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import java.net.InetAddress;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlatformAdapterAcceptanceTest {

  private static final MachineId ANY_MACHINE_ID = new MachineId(new byte[0]);

  private static final Integer NULL_PORT = null;

  private static final String SOME_USERNAME = "some-username";
  private static final String SOME_MESSAGE = "some-message";

  private static final MachineProfileName SOME_PROFILE_NAME = new MachineProfileName(
      "some-profile");
  private static final MachineNetworkName SOME_NETWORK_NAME = new MachineNetworkName(
      "some-network");
  private static final InetAddress SOME_MACHINE_HOST = getLocalHost();

  @Mock
  private PlatformEventSource eventSource;

  @Mock
  private MachineCatalog catalog;

  @Mock
  private MachineSessionFactory sessionFactory;

  private PlatformAdapter adapter;

  @BeforeEach
  public void setUp() {
    adapter = new PlatformAdapter(
        eventSource,
        catalog,
        sessionFactory);
  }

  @Test
  public void saySomething(
      @Mock MachineProfile someProfile,
      @Mock MachineRealm someRealm,
      @Mock MachineSessionAuthentication someAuthentication,
      @Mock MachineSessionPool someSessionPool,
      @Mock MachineBuilder someBuilder,
      @Mock MachineSession someSession,
      @Mock MachineLink someLink) throws Exception {
    doAnswer(invocation -> {
      try {
        MachineAvailableEvent event = invocation.getArgument(0, MachineAvailableEvent.class);
        adapter.getContext()
            .lookup(event.getMachineRef())
            .flatMap(facetGet(TestSayMessageMachineFacet.class))
            .ifPresent(facet -> facet.sayMessage(SOME_USERNAME, SOME_MESSAGE));
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }).when(eventSource).fireEvent(any(MachineAvailableEvent.class));

    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.of(someProfile));
    when(someProfile.getNetworkName())
        .thenReturn(SOME_NETWORK_NAME);
    when(someProfile.getSessionPort())
        .thenReturn(Optional.empty());
    when(someProfile.getRealm())
        .thenReturn(someRealm);
    when(someRealm.authentication(SOME_USERNAME))
        .thenReturn(Optional.of(someAuthentication));
    when(someProfile.getSessionPool())
        .thenReturn(someSessionPool);
    when(someSessionPool.getIdleTimeout())
        .thenReturn(Optional.empty());
    when(someProfile.getBuilder())
        .thenReturn(someBuilder);
    doAnswer(invocation -> {
      MachineBuilderContext builderContext = invocation.getArgument(0, MachineBuilderContext.class);
      builderContext.register(new TestSayMessageMachineFacetFactory());
      return null;
    }).when(someBuilder).build(any());

    when(sessionFactory.getSession(
        SOME_MACHINE_HOST,
        NULL_PORT,
        SOME_USERNAME,
        someAuthentication)).thenReturn(someSession);

    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(someLink.getSessionHost(SOME_NETWORK_NAME))
        .thenReturn(SOME_MACHINE_HOST);

    TestPlatformPort testPort = new TestPlatformPort();
    Thread testPortListenThread = new Thread(() -> {
      try {
        adapter.listen(testPort);
      } catch (InterruptedException e) {
        currentThread().interrupt();
      }
    });
    testPortListenThread.start();

    testPort.accept(ANY_MACHINE_ID, someLink);

    await().untilAsserted(() -> {
      verify(someSession).execute(new TestSayMessageCommand(SOME_MESSAGE));
    });

    testPortListenThread.interrupt();
  }
}
