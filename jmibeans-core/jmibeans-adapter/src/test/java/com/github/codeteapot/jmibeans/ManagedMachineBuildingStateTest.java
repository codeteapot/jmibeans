package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.util.logging.Level.SEVERE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.testing.logging.LoggerStub;

@ExtendWith(MockitoExtension.class)
class ManagedMachineBuildingStateTest {

  private static final boolean MAY_INTERRUPT_IF_RUNNING = true;

  private static final MachineRef SOME_REF = new MachineRef(new byte[] {1}, new byte[] {3});

  private static final TestFooMachineFacet SOME_FACET = new TestFooMachineFacet();

  private static final MachineBuildingException SOME_BUILDING_EXCEPTION =
      new MachineBuildingException(new Exception());


  private LoggerStub loggerStub;

  @Mock
  private Handler loggerHandler;

  @Mock
  private ManagedMachineStateChanger stateChanger;

  @Mock
  private Consumer<MachineRef> removalAction;

  @Mock
  private MachineAgent agent;

  @Mock
  private PlatformEventTarget eventTarget;

  private Set<Object> facets;

  @Mock
  private ExecutorService builderExecutor;

  @Mock
  private MachineBuilder builder;

  @Mock
  private ManagedMachineAvailableStateConstructor availableStateConstructor;

  @Mock
  private ManagedMachineDisposedStateConstructor disposedStateConstructor;

  @BeforeEach
  void setUp() {
    loggerStub = loggerStubFor(ManagedMachineBuildingState.class.getName(), loggerHandler);
    facets = new HashSet<>();
  }

  @AfterEach
  void tearDown() {
    loggerStub.restore();
  }

  @Test
  void createBuildSuccess(@Mock ManagedMachineAvailableState someAvailableState) throws Exception {
    when(builderExecutor.submit(Mockito.<Callable<?>>any()))
        .thenAnswer(invocation -> invocation.getArgument(0, Callable.class).call());
    when(availableStateConstructor.construct(stateChanger, SOME_REF, eventTarget, facets))
        .thenReturn(someAvailableState);
    ManagedMachineBuildingState state = new ManagedMachineBuildingState(
        stateChanger,
        SOME_REF,
        removalAction,
        agent,
        eventTarget,
        facets,
        builderExecutor,
        builder,
        availableStateConstructor,
        disposedStateConstructor);

    InOrder order = inOrder(builder, stateChanger, eventTarget);
    order.verify(builder).build(state);
    order.verify(stateChanger).changeState(someAvailableState);
    order.verify(eventTarget).fireAvailableEvent(argThat(event -> event.getSource().equals(state)
        && event.getMachineRef().equals(SOME_REF)));
  }

  @Test
  void createBuildFailure() throws Exception {
    when(builderExecutor.submit(Mockito.<Callable<?>>any()))
        .thenAnswer(invocation -> invocation.getArgument(0, Callable.class).call());
    doThrow(SOME_BUILDING_EXCEPTION)
        .when(builder).build(any());
    new ManagedMachineBuildingState(
        stateChanger,
        SOME_REF,
        removalAction,
        agent,
        eventTarget,
        facets,
        builderExecutor,
        builder,
        availableStateConstructor,
        disposedStateConstructor);

    verify(removalAction).accept(SOME_REF);
    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(SEVERE) &&
        record.getThrown().equals(SOME_BUILDING_EXCEPTION)));
  }

  @Test
  void registerFacet() {
    ManagedMachineBuildingState state = new ManagedMachineBuildingState(
        stateChanger,
        SOME_REF,
        removalAction,
        agent,
        eventTarget,
        facets,
        builderExecutor,
        builder,
        availableStateConstructor,
        disposedStateConstructor);

    state.registerFacet(SOME_FACET);

    assertThat(facets).containsOnly(SOME_FACET);
  }

  @Test
  void getAgent() {
    ManagedMachineBuildingState state = new ManagedMachineBuildingState(
        stateChanger,
        SOME_REF,
        removalAction,
        agent,
        eventTarget,
        facets,
        builderExecutor,
        builder,
        availableStateConstructor,
        disposedStateConstructor);

    MachineAgent result = state.getAgent();

    assertThat(result).isEqualTo(agent);
  }

  @Test
  void cannotGetFacet() {
    ManagedMachineBuildingState state = new ManagedMachineBuildingState(
        stateChanger,
        SOME_REF,
        removalAction,
        agent,
        eventTarget,
        facets,
        builderExecutor,
        builder,
        availableStateConstructor,
        disposedStateConstructor);

    Throwable e = catchThrowable(() -> state.getFacet(TestFooMachineFacet.class));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void isNotReady() {
    ManagedMachineBuildingState state = new ManagedMachineBuildingState(
        stateChanger,
        SOME_REF,
        removalAction,
        agent,
        eventTarget,
        facets,
        builderExecutor,
        builder,
        availableStateConstructor,
        disposedStateConstructor);

    boolean ready = state.isReady();

    assertThat(ready).isFalse();
  }

  @Test
  void cannotBuild(
      @Mock Consumer<MachineRef> anyRemovalAction,
      @Mock ExecutorService anyBuilderExecutor,
      @Mock MachineBuilder anyBuilder,
      @Mock MachineAgent anyAgent) {
    ManagedMachineBuildingState state = new ManagedMachineBuildingState(
        stateChanger,
        SOME_REF,
        removalAction,
        agent,
        eventTarget,
        facets,
        builderExecutor,
        builder,
        availableStateConstructor,
        disposedStateConstructor);

    Throwable e = catchThrowable(() -> state.build(
        anyRemovalAction,
        anyBuilderExecutor,
        anyBuilder,
        anyAgent));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void disposeBeforeBuilt(
      @Mock Future<Object> someTask,
      @Mock ManagedMachineDisposedState disposedState) {
    when(builderExecutor.submit(Mockito.<Callable<Object>>any()))
        .thenReturn(someTask);
    when(disposedStateConstructor.construct(stateChanger, SOME_REF))
        .thenReturn(disposedState);
    ManagedMachineBuildingState state = new ManagedMachineBuildingState(
        stateChanger,
        SOME_REF,
        removalAction,
        agent,
        eventTarget,
        facets,
        builderExecutor,
        builder,
        availableStateConstructor,
        disposedStateConstructor);

    state.dispose();

    InOrder order = inOrder(stateChanger, removalAction, eventTarget, someTask);
    order.verify(stateChanger).changeState(disposedState);
    order.verify(someTask).cancel(MAY_INTERRUPT_IF_RUNNING);
    order.verify(removalAction).accept(SOME_REF);
  }
}
