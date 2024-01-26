package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.hamcrest.SomeLogRecordMatcher.someLogRecord;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.isEqual;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.ReferencedMachine;
import com.github.codeteapot.jmibeans.port.MachineBuilderPropertyResolver;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineProfile;
import com.github.codeteapot.testing.logging.junit.jupiter.LoggingExtension;
import com.github.codeteapot.testing.logging.mockito.MockLogger;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Handler;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
class MachineContainerTest {

  private static final MachineProfileName ANY_PROFILE_NAME = new MachineProfileName("any-profile");

  private static final boolean READY = true;
  private static final boolean NOT_READY = false;

  private static final MachineRef SOME_REF = new MachineRef(new byte[] {1}, new byte[] {5});
  private static final String SOME_REF_STR = "01:05";
  private static final MachineRef ANOTHER_REF = new MachineRef(new byte[] {2}, new byte[] {6});

  private static final MachineProfileName SOME_PROFILE_NAME =
      new MachineProfileName("some-profile");

  private static final RuntimeException SOME_UNEXPECTED_EXCEPTION = new RuntimeException();

  @MockLogger(name = "com.github.codeteapot.jmibeans.MachineContainer")
  private Handler loggerHandler;

  @Mock
  private PlatformEventTarget eventTarget;

  @Mock
  private MachineCatalog catalog;

  @Mock
  private ExecutorService builderExecutor;

  private ConcurrentMap<MachineRef, ManagedMachine> machineMap;

  @Mock
  private ManagedMachineConstructor machineConstructor;

  @Mock
  private ManagedMachineBuildingJobConstructor builderJobConstructor;

  private MachineContainer container;

  @BeforeEach
  void setUp() {
    machineMap = new ConcurrentHashMap<>();
    container = new MachineContainer(
        eventTarget,
        catalog,
        builderExecutor,
        machineMap,
        machineConstructor,
        builderJobConstructor);
  }

  @Test
  void availableReadyOnly(
      @Mock ManagedMachine someMachine,
      @Mock TestFooMachineFacet someFooFacet,
      @Mock ManagedMachine anotherMachine) {
    when(someMachine.isReady()).thenReturn(READY);
    when(someMachine.getFacet(TestFooMachineFacet.class)).thenReturn(ofNullable(someFooFacet));
    when(someMachine.referenced()).thenReturn(new ReferencedMachineImpl(SOME_REF, someMachine));
    when(anotherMachine.isReady()).thenReturn(NOT_READY);
    machineMap.put(SOME_REF, someMachine);
    machineMap.put(ANOTHER_REF, anotherMachine);

    Stream<ReferencedMachine> available = container.available();

    assertThat(available).hasSize(1).allSatisfy(machine -> {
      assertThat(machine.getRef()).isEqualTo(SOME_REF);
      // TODO Check machine instance through constructor, not through features
      assertThat(machine.getFacet(TestFooMachineFacet.class)).hasValue(someFooFacet);
      assertThat(machine.getFacet(TestBarMachineFacet.class)).isEmpty();
    });
  }

  @Test
  void lookupReady(@Mock ManagedMachine someMachine) {
    when(someMachine.isReady()).thenReturn(READY);
    machineMap.put(SOME_REF, someMachine);

    Optional<Machine> machine = container.lookup(SOME_REF);

    assertThat(machine).hasValue(someMachine);
  }

  @Test
  void lookupNotReady(@Mock ManagedMachine someMachine) {
    when(someMachine.isReady()).thenReturn(NOT_READY);
    machineMap.put(ANOTHER_REF, someMachine);

    Optional<Machine> machine = container.lookup(ANOTHER_REF);

    assertThat(machine).isEmpty();
  }

  @Test
  void lookupNotFound(@Mock ManagedMachine someMachine) {
    machineMap.put(SOME_REF, someMachine);

    Optional<Machine> machine = container.lookup(ANOTHER_REF);

    assertThat(machine).isEmpty();
  }

  @Test
  void acceptWithProfile(
      @Mock MachineLink someLink,
      @Mock MachineBuilderPropertyResolver someBuilderPropertyResolver,
      @Mock MachineAgent someAgent,
      @Mock MachineProfile someProfile,
      @Mock MachineBuilder someBuilder,
      @Mock ManagedMachineBuildingJob someBuilderJob,
      @Mock ManagedMachine someMachine) {
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.of(someProfile));
    when(someProfile.getBuilder())
        .thenReturn(someBuilder);
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(someLink.getBuilderPropertyResolver())
        .thenReturn(someBuilderPropertyResolver);
    when(someLink.getAgent())
        .thenReturn(someAgent);
    when(builderJobConstructor.construct(
        builderExecutor,
        someBuilder)).thenReturn(someBuilderJob);
    when(machineConstructor.construct(
        SOME_REF,
        eventTarget,
        someBuilderPropertyResolver,
        someAgent,
        someBuilderJob)).thenReturn(someMachine);

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).containsEntry(SOME_REF, someMachine);
  }

  @Test
  void acceptWithProfileRepeated(
      @Mock ManagedMachine someMachine,
      @Mock MachineLink anyLink,
      @Mock MachineProfile anyProfile,
      @Mock MachineBuilderPropertyResolver anyBuilderPropertyResolver,
      @Mock MachineAgent anyAgent) {
    when(catalog.getProfile(any()))
        .thenReturn(Optional.of(anyProfile));
    when(anyLink.getProfileName())
        .thenReturn(ANY_PROFILE_NAME);
    when(anyLink.getBuilderPropertyResolver())
        .thenReturn(anyBuilderPropertyResolver);
    when(anyLink.getAgent())
        .thenReturn(anyAgent);
    machineMap.put(SOME_REF, someMachine);

    container.accept(SOME_REF, anyLink);

    assertThat(machineMap).containsEntry(SOME_REF, someMachine);
    verify(machineConstructor, never()).construct(any(), any(), any(), any(), any());
  }

  @Test
  void acceptWithoutProfile(
      @Mock MachineLink someLink,
      @Mock MachineBuilderPropertyResolver anyBuilderPropertyResolver,
      @Mock MachineAgent anyAgent) {
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.empty());
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(someLink.getBuilderPropertyResolver())
        .thenReturn(anyBuilderPropertyResolver);
    when(someLink.getAgent())
        .thenReturn(anyAgent);

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).isEmpty();
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(containsString(SOME_REF_STR))));
  }

  @Test
  void acceptWithUnexpectedError(
      @Mock MachineLink someLink,
      @Mock MachineProfile someProfile,
      @Mock MachineBuilder someBuilder,
      @Mock MachineBuilderPropertyResolver anyBuilderPropertyResolver,
      @Mock MachineAgent anyAgent) {
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.of(someProfile));
    when(someProfile.getBuilder())
        .thenThrow(SOME_UNEXPECTED_EXCEPTION);
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(someLink.getBuilderPropertyResolver())
        .thenReturn(anyBuilderPropertyResolver);
    when(someLink.getAgent())
        .thenReturn(anyAgent);

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).isEmpty();
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(containsString(SOME_REF_STR))
        .withThrown(equalTo(SOME_UNEXPECTED_EXCEPTION))));
  }

  @Test
  void forgetExisting(@Mock ManagedMachine someMachine) {
    machineMap.put(SOME_REF, someMachine);

    container.forget(SOME_REF);

    assertThat(machineMap).isEmpty();
    verify(someMachine).dispose();
  }

  @Test
  void forgetWithUnexpectedError(@Mock ManagedMachine someMachine) {
    doThrow(SOME_UNEXPECTED_EXCEPTION).when(someMachine).dispose();
    machineMap.put(SOME_REF, someMachine);

    container.forget(SOME_REF);

    assertThat(machineMap).isEmpty();
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(containsString(SOME_REF_STR))
        .withThrown(equalTo(SOME_UNEXPECTED_EXCEPTION))));
  }

  @Test
  void forgetMatching(@Mock ManagedMachine someMachine, @Mock ManagedMachine anotherMachine) {
    machineMap.put(SOME_REF, someMachine);
    machineMap.put(ANOTHER_REF, anotherMachine);

    container.forgetAll(isEqual(SOME_REF));

    assertThat(machineMap).containsEntry(ANOTHER_REF, anotherMachine);
    verify(someMachine).dispose();
    verify(anotherMachine, never()).dispose();
  }
}
