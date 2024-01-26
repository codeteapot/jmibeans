package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Handler;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineProfile;
import com.github.codeteapot.testing.logging.LoggerStub;

@ExtendWith(MockitoExtension.class)
class MachineContainerTest {

  private static final boolean READY = true;
  private static final boolean NOT_READY = false;

  private static final MachineRef SOME_REF = new MachineRef(new byte[] {1}, new byte[] {3});
  private static final String SOME_REF_STR = "01:03";
  private static final MachineRef ANOTHER_REF = new MachineRef(new byte[] {2}, new byte[] {4});

  private static final MachineProfileName SOME_PROFILE_NAME = new MachineProfileName("some-name");

  private LoggerStub loggerStub;

  @Mock
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

  private MachineContainer container;

  @BeforeEach
  void setUp() {
    loggerStub = loggerStubFor(MachineContainer.class.getName(), loggerHandler);
    machineMap = new ConcurrentHashMap<>();
    container = new MachineContainer(
        eventTarget,
        catalog,
        builderExecutor,
        machineMap,
        machineConstructor);
  }

  @AfterEach
  void tearDown() {
    loggerStub.restore();
  }

  @Test
  void availableReadyOnly(@Mock ManagedMachine someMachine, @Mock ManagedMachine anotherMachine) {
    when(someMachine.isReady()).thenReturn(READY);
    when(anotherMachine.isReady()).thenReturn(NOT_READY);
    machineMap.put(SOME_REF, someMachine);
    machineMap.put(ANOTHER_REF, anotherMachine);

    Stream<Machine> available = container.available();

    assertThat(available).containsOnly(someMachine);
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
  void acceptWithProfileNew(
      @Mock MachineLink someLink,
      @Mock MachineAgent someAgent,
      @Mock MachineProfile someProfile,
      @Mock MachineBuilder someBuilder,
      @Mock ManagedMachine someMachine) {
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.of(someProfile));
    when(someProfile.getBuilder())
        .thenReturn(someBuilder);
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(someLink.getAgent())
        .thenReturn(someAgent);
    when(machineConstructor.construct(SOME_REF, eventTarget))
        .thenReturn(someMachine);

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).containsEntry(SOME_REF, someMachine);
    verify(someMachine).build(any(), eq(builderExecutor), eq(someBuilder), eq(someAgent));
  }

  @Test
  void acceptWithProfileRepeated(
      @Mock MachineLink someLink,
      @Mock MachineAgent someAgent,
      @Mock MachineProfile someProfile,
      @Mock MachineBuilder someBuilder,
      @Mock ManagedMachine someMachine) {
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.of(someProfile));
    when(someProfile.getBuilder())
        .thenReturn(someBuilder);
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);
    when(someLink.getAgent())
        .thenReturn(someAgent);
    machineMap.put(SOME_REF, someMachine);

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).containsEntry(SOME_REF, someMachine);
    verify(someMachine).build(any(), eq(builderExecutor), eq(someBuilder), eq(someAgent));
  }

  @Test
  void acceptWithoutProfile(@Mock MachineLink someLink) {
    when(catalog.getProfile(SOME_PROFILE_NAME))
        .thenReturn(Optional.empty());
    when(someLink.getProfileName())
        .thenReturn(SOME_PROFILE_NAME);

    container.accept(SOME_REF, someLink);

    assertThat(machineMap).isEmpty();
    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING) &&
        record.getMessage().contains(SOME_REF_STR)));
  }

  @Test
  void forgetExisting(@Mock ManagedMachine someMachine) {
    machineMap.put(SOME_REF, someMachine);

    container.forget(SOME_REF);

    assertThat(machineMap).isEmpty();
    verify(someMachine).dispose();
  }
}
