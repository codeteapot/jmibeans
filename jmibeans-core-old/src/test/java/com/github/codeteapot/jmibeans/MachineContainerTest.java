package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.Machine;
import com.github.codeteapot.jmibeans.MachineAvailableEventConstructor;
import com.github.codeteapot.jmibeans.MachineBuilder;
import com.github.codeteapot.jmibeans.MachineBuildingException;
import com.github.codeteapot.jmibeans.MachineCatalog;
import com.github.codeteapot.jmibeans.MachineContainer;
import com.github.codeteapot.jmibeans.MachineContainerAction;
import com.github.codeteapot.jmibeans.MachineContainerActionPerformer;
import com.github.codeteapot.jmibeans.MachineContextConstructor;
import com.github.codeteapot.jmibeans.MachineLostEventConstructor;
import com.github.codeteapot.jmibeans.MachineProfile;
import com.github.codeteapot.jmibeans.MachineRealm;
import com.github.codeteapot.jmibeans.MachineSessionPool;
import com.github.codeteapot.jmibeans.ManagedMachine;
import com.github.codeteapot.jmibeans.ManagedMachineFactory;
import com.github.codeteapot.jmibeans.ManagedMachineFactoryConstructor;
import com.github.codeteapot.jmibeans.PlatformEventSource;
import com.github.codeteapot.jmibeans.PooledMachineSessionFactory;
import com.github.codeteapot.jmibeans.PooledMachineSessionFactoryConstructor;
import com.github.codeteapot.jmibeans.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.machine.MachineContext;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import com.github.codeteapot.testing.logging.LoggerStub;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Handler;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MachineContainerTest {

  private static final MachineRef SOME_REF = new MachineRef(new byte[] {0x0a}, new byte[] {0x01});
  private static final MachineRef ANOTHER_REF = new MachineRef(
      new byte[] {0x0a},
      new byte[] {0x03});

  private static final MachineProfileName SOME_PROFILE_NAME = new MachineProfileName("some-value");
  private static final MachineNetworkName SOME_NETWORK_NAME = new MachineNetworkName("some-value");
  private static final int SOME_SESSION_PORT = 1234;

  private static final MachineBuildingException SOME_EXPECTED_BUILDING_EXCEPTION =
      new MachineBuildingException("any-message");
  private static final RuntimeException SOME_UNEXPECTED_BUILDING_EXCEPTION = new RuntimeException();

  private LoggerStub loggerStub;

  @Mock
  private Handler loggerHandler;

  @Mock
  private PlatformEventSource eventSource;

  @Mock
  private MachineCatalog catalog;

  @Mock
  private MachineSessionFactory sessionFactory;

  @Mock
  private MachineContainerActionPerformer actionPerformer;

  private Map<MachineRef, ManagedMachine> machineMap;

  @Mock
  private ScheduledExecutorService sessionPoolReleaseExecutor;

  @Mock
  private PooledMachineSessionFactoryConstructor pooledSessionFactoryConstructor;

  @Mock
  private MachineContextConstructor contextConstructor;

  @Mock
  private ManagedMachineFactoryConstructor managedFactoryConstructor;

  @Mock
  private MachineAvailableEventConstructor availableEventConstructor;

  @Mock
  private MachineLostEventConstructor lostEventConstructor;

  private MachineContainer container;

  @BeforeEach
  public void setUp() {
    loggerStub = loggerStubFor(MachineContainer.class.getName(), loggerHandler);
    machineMap = new HashMap<>();
    container = new MachineContainer(
        eventSource,
        catalog,
        sessionFactory,
        actionPerformer,
        machineMap,
        sessionPoolReleaseExecutor,
        pooledSessionFactoryConstructor,
        contextConstructor,
        managedFactoryConstructor,
        availableEventConstructor,
        lostEventConstructor);
  }

  @AfterEach
  public void tearDown() {
    loggerStub.restore();
  }

  @Test
  public void lookupExisting(@Mock ManagedMachine someMachine) {
    machineMap.put(SOME_REF, someMachine);

    Optional<Machine> machine = container.lookup(SOME_REF);

    assertThat(machine).hasValue(someMachine);
  }

  @Test
  public void lookupNonExisting(@Mock ManagedMachine someMachine) {
    machineMap.put(SOME_REF, someMachine);

    Optional<Machine> machine = container.lookup(ANOTHER_REF);

    assertThat(machine).isEmpty();
  }

  @Test
  public void availableFromCopy(
      @Mock ManagedMachine someMachine,
      @Mock ManagedMachine anotherMachine) {
    machineMap.put(SOME_REF, someMachine);

    Stream<Machine> available = container.available();
    machineMap.remove(SOME_REF);
    machineMap.put(SOME_REF, anotherMachine);

    assertThat(available)
        .contains(someMachine)
        .doesNotContain(anotherMachine);
  }

  @Test
  public void acceptMachineWithProfile(
      @Mock MachineLink someLink,
      @Mock MachineProfile someProfile,
      @Mock MachineSessionPool someSessionPool,
      @Mock MachineRealm someRealm,
      @Mock MachineBuilder someBuilder,
      @Mock PooledMachineSessionFactory somePooledSessionFactory,
      @Mock MachineContext someContext,
      @Mock ManagedMachineFactory someManagedFactory,
      @Mock ManagedMachine someManagedMachine,
      @Mock MachineAvailableEvent someAvailableEvent) throws Exception {
    doAnswer(invocation -> {
      MachineContainerAction action = (MachineContainerAction) invocation.getArgument(1);
      action.perform();
      return null;
    }).when(actionPerformer).perform(eq(SOME_REF), any());
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.of(someProfile));
    when(someProfile.getSessionPool())
        .thenReturn(someSessionPool);
    when(someProfile.getRealm())
        .thenReturn(someRealm);
    when(someProfile.getNetworkName())
        .thenReturn(SOME_NETWORK_NAME);
    when(someProfile.getSessionPort())
        .thenReturn(Optional.of(SOME_SESSION_PORT));
    when(someProfile.getBuilder())
        .thenReturn(someBuilder);
    when(pooledSessionFactoryConstructor.construct(
        sessionFactory,
        sessionPoolReleaseExecutor,
        SOME_REF,
        someSessionPool)).thenReturn(somePooledSessionFactory);
    when(contextConstructor.construct(
        SOME_REF,
        someRealm,
        someLink,
        SOME_NETWORK_NAME,
        SOME_SESSION_PORT,
        somePooledSessionFactory)).thenReturn(someContext);
    when(managedFactoryConstructor.construct(someContext))
        .thenReturn(someManagedFactory);
    when(someManagedFactory.getMachine(somePooledSessionFactory))
        .thenReturn(someManagedMachine);
    when(availableEventConstructor.construct(container, SOME_REF))
        .thenReturn(someAvailableEvent);

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).containsEntry(SOME_REF, someManagedMachine);
    InOrder order = inOrder(eventSource, someManagedFactory);
    order.verify(someManagedFactory).build(someBuilder);
    order.verify(eventSource).fireEvent(someAvailableEvent); // It is recommended to be done after
                                                             // updating machine map
  }

  @Test
  public void logSevereWhenAcceptMachineWithProfileAndExpectedFailure(
      @Mock MachineLink someLink,
      @Mock MachineProfile someProfile,
      @Mock MachineSessionPool someSessionPool,
      @Mock MachineRealm someRealm,
      @Mock MachineBuilder someBuilder,
      @Mock PooledMachineSessionFactory somePooledSessionFactory,
      @Mock MachineContext someContext,
      @Mock ManagedMachineFactory someManagedFactory) throws Exception {
    doAnswer(invocation -> {
      MachineContainerAction action = (MachineContainerAction) invocation.getArgument(1);
      action.perform();
      return null;
    }).when(actionPerformer).perform(eq(SOME_REF), any());
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.of(someProfile));
    when(someProfile.getSessionPool())
        .thenReturn(someSessionPool);
    when(someProfile.getRealm())
        .thenReturn(someRealm);
    when(someProfile.getNetworkName())
        .thenReturn(SOME_NETWORK_NAME);
    when(someProfile.getSessionPort())
        .thenReturn(Optional.of(SOME_SESSION_PORT));
    when(someProfile.getBuilder())
        .thenReturn(someBuilder);
    when(pooledSessionFactoryConstructor.construct(
        sessionFactory,
        sessionPoolReleaseExecutor,
        SOME_REF,
        someSessionPool)).thenReturn(somePooledSessionFactory);
    when(contextConstructor.construct(
        SOME_REF,
        someRealm,
        someLink,
        SOME_NETWORK_NAME,
        SOME_SESSION_PORT,
        somePooledSessionFactory)).thenReturn(someContext);
    when(managedFactoryConstructor.construct(someContext))
        .thenReturn(someManagedFactory);
    doThrow(SOME_EXPECTED_BUILDING_EXCEPTION)
        .when(someManagedFactory).build(someBuilder);

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).isEmpty();
    verify(eventSource, never()).fireEvent(any(MachineAvailableEvent.class));
    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(SEVERE) &&
        record.getThrown().equals(SOME_EXPECTED_BUILDING_EXCEPTION)));
  }

  @Test
  public void logSevereWhenAcceptMachineWithProfileAndUnexpectedFailure(
      @Mock MachineLink someLink,
      @Mock MachineProfile someProfile,
      @Mock MachineSessionPool someSessionPool,
      @Mock MachineRealm someRealm,
      @Mock MachineBuilder someBuilder,
      @Mock PooledMachineSessionFactory somePooledSessionFactory,
      @Mock MachineContext someContext,
      @Mock ManagedMachineFactory someManagedFactory) throws Exception {
    doAnswer(invocation -> {
      MachineContainerAction action = (MachineContainerAction) invocation.getArgument(1);
      action.perform();
      return null;
    }).when(actionPerformer).perform(eq(SOME_REF), any());
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.of(someProfile));
    when(someProfile.getSessionPool())
        .thenReturn(someSessionPool);
    when(someProfile.getRealm())
        .thenReturn(someRealm);
    when(someProfile.getNetworkName())
        .thenReturn(SOME_NETWORK_NAME);
    when(someProfile.getSessionPort())
        .thenReturn(Optional.of(SOME_SESSION_PORT));
    when(someProfile.getBuilder())
        .thenReturn(someBuilder);
    when(pooledSessionFactoryConstructor.construct(
        sessionFactory,
        sessionPoolReleaseExecutor,
        SOME_REF,
        someSessionPool)).thenReturn(somePooledSessionFactory);
    when(contextConstructor.construct(
        SOME_REF,
        someRealm,
        someLink,
        SOME_NETWORK_NAME,
        SOME_SESSION_PORT,
        somePooledSessionFactory)).thenReturn(someContext);
    when(managedFactoryConstructor.construct(someContext))
        .thenReturn(someManagedFactory);
    doThrow(SOME_UNEXPECTED_BUILDING_EXCEPTION)
        .when(someManagedFactory).build(someBuilder);

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).isEmpty();
    verify(eventSource, never()).fireEvent(any(MachineAvailableEvent.class));
    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(SEVERE) &&
        record.getThrown().equals(SOME_UNEXPECTED_BUILDING_EXCEPTION)));
  }

  @Test
  public void logWarningWhenAcceptAlreadyExistingMachine(
      @Mock ManagedMachine someMachine,
      @Mock MachineLink anyLink) {
    doAnswer(invocation -> {
      MachineContainerAction action = (MachineContainerAction) invocation.getArgument(1);
      action.perform();
      return null;
    }).when(actionPerformer).perform(eq(SOME_REF), any());
    machineMap.put(SOME_REF, someMachine);

    container.accept(SOME_REF, anyLink);

    assertThat(machineMap).containsEntry(SOME_REF, someMachine);
    verify(eventSource, never()).fireEvent(any(MachineAvailableEvent.class));
    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)));
  }

  @Test
  public void logWarningWhenAcceptWithoutProfile(@Mock MachineLink someLink) {
    doAnswer(invocation -> {
      MachineContainerAction action = (MachineContainerAction) invocation.getArgument(1);
      action.perform();
      return null;
    }).when(actionPerformer).perform(eq(SOME_REF), any());
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.empty());

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).isEmpty();
    verify(eventSource, never()).fireEvent(any(MachineAvailableEvent.class));
    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)));
  }

  @Test
  public void forgetMachine(@Mock ManagedMachine someMachine,
      @Mock MachineLostEvent someLostEvent) {
    doAnswer(invocation -> {
      MachineContainerAction action = (MachineContainerAction) invocation.getArgument(1);
      action.perform();
      return null;
    }).when(actionPerformer).perform(eq(SOME_REF), any());
    when(lostEventConstructor.construct(container, SOME_REF))
        .thenReturn(someLostEvent);
    machineMap.put(SOME_REF, someMachine);

    container.forget(SOME_REF);

    assertThat(machineMap).doesNotContainKey(SOME_REF);
    verify(eventSource).fireEvent(someLostEvent); // It is recommended to be done after updating
                                                  // machine map
  }

  @Test
  public void logWarningWhenForgetNonExistingMachine() {
    doAnswer(invocation -> {
      MachineContainerAction action = (MachineContainerAction) invocation.getArgument(1);
      action.perform();
      return null;
    }).when(actionPerformer).perform(eq(SOME_REF), any());

    container.forget(SOME_REF);

    verify(eventSource, never()).fireEvent(any(MachineLostEvent.class));
    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)));
  }
}
