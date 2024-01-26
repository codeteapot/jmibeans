package com.github.codeteapot.jmibeans;

import static java.util.Collections.emptyIterator;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperties;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuildingResult;
import com.github.codeteapot.jmibeans.profile.MachineProfile;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlatformAdapterAcceptanceTest {

  private static final byte[] SOME_MACHINE_ID = {1, 3};
  private static final MachineProfileName SOME_PROFILE_NAME = new MachineProfileName("some-name");

  @Mock
  private PlatformEventTarget eventTarget;

  @Mock
  private MachineCatalog catalog;

  private PlatformAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new PlatformAdapter(eventTarget, catalog, newCachedThreadPool());
  }

  @Test
  void forgetWhileBuilding(
      @Mock MachineProfile someProfile,
      @Mock MachineBuilder someBuilder,
      @Mock MachineLink someLink,
      @Mock MachineBuilderProperties someBuilderProperties,
      @Mock MachineAgent someAgent,
      @Mock MachineBuildingResult someBuilderResult) throws Exception {
    ExecutorService listenExecutor = newSingleThreadExecutor();
    TestPlatformPort testPort = new TestPlatformPort();

    Semaphore buildStarted = new Semaphore(0);
    Semaphore buildEndless = new Semaphore(0);

    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.of(someProfile));
    when(someProfile.getBuilder())
        .thenReturn(someBuilder);
    when(someBuilderProperties.iterator()).thenAnswer(invocation -> emptyIterator());
    doAnswer(invocation -> {
      buildStarted.release();
      buildEndless.acquire();
      return someBuilderResult;
    }).when(someBuilder).build(argThat(context -> context.getAgent().equals(someAgent)));
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(someLink.getBuilderProperties())
        .thenReturn(someBuilderProperties);
    when(someLink.getAgent())
        .thenReturn(someAgent);

    listenExecutor.submit(() -> {
      adapter.listen(testPort);
      return null;
    });

    testPort.accept(SOME_MACHINE_ID, someLink);
    buildStarted.acquire();
    testPort.forget(SOME_MACHINE_ID);

    await().untilAsserted(() -> {
      verify(eventTarget, never()).fireAvailable(any());
      verify(eventTarget, never()).fireLost(any());
    });

    listenExecutor.shutdown();
  }
}
