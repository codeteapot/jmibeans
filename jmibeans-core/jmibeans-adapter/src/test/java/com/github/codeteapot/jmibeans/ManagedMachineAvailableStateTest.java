package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.logging.Handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.testing.logging.LoggerStub;

@ExtendWith(MockitoExtension.class)
class ManagedMachineAvailableStateTest {

  private static final MachineRef SOME_REF = new MachineRef(new byte[] {1}, new byte[] {3});

  private static final TestFooMachineFacet SOME_FOO_FACET = new TestFooMachineFacet();

  private static final RuntimeException SOME_DISPOSE_EXCEPTION = new RuntimeException();

  private LoggerStub loggerStub;

  @Mock
  private Handler loggerHandler;

  @Mock
  private ManagedMachineStateChanger stateChanger;

  @Mock
  private Consumer<MachineRef> removalAction;

  @Mock
  private PlatformEventTarget eventTarget;

  private Set<Object> facets;

  @Mock
  private ManagedMachineDisposedStateConstructor disposedStateConstructor;

  private ManagedMachineAvailableState state;

  @BeforeEach
  void setUp() {
    loggerStub = loggerStubFor(ManagedMachineAvailableState.class.getName(), loggerHandler);
    facets = new HashSet<>();
    state = new ManagedMachineAvailableState(
        stateChanger,
        SOME_REF,
        eventTarget,
        facets,
        disposedStateConstructor);
  }

  @AfterEach
  void tearDown() {
    loggerStub.restore();
  }

  @Test
  void getKnownFacet() {
    facets.add(SOME_FOO_FACET);

    Optional<TestFooMachineFacet> facet = state.getFacet(TestFooMachineFacet.class);

    assertThat(facet).hasValue(SOME_FOO_FACET);
  }

  @Test
  void getUnknownFacet() {
    facets.add(SOME_FOO_FACET);

    Optional<TestBarMachineFacet> facet = state.getFacet(TestBarMachineFacet.class);

    assertThat(facet).isEmpty();
  }

  @Test
  void isReady() {
    boolean ready = state.isReady();

    assertThat(ready).isTrue();
  }

  @Test
  void cannotBuild(
      @Mock Consumer<MachineRef> anyRemovalAction,
      @Mock ExecutorService anyBuilderExecutor,
      @Mock MachineBuilder anyBuilder,
      @Mock MachineAgent anyAgent) {
    Throwable e = catchThrowable(() -> state.build(
        anyRemovalAction,
        anyBuilderExecutor,
        anyBuilder,
        anyAgent));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void dispose(
      @Mock ManagedMachineDisposedState disposedState,
      @Mock TestDisposableMachineFacet successDisposableFacet,
      @Mock TestDisposableMachineFacet failureDisposableFacet) {
    when(disposedStateConstructor.construct(stateChanger, SOME_REF))
        .thenReturn(disposedState);
    doThrow(SOME_DISPOSE_EXCEPTION)
        .when(failureDisposableFacet).dispose();
    facets.add(failureDisposableFacet);
    facets.add(successDisposableFacet);

    state.dispose();

    InOrder order = inOrder(
        stateChanger,
        eventTarget,
        successDisposableFacet,
        failureDisposableFacet);
    order.verify(stateChanger).changeState(disposedState);
    order.verify(eventTarget).fireLostEvent(argThat(event -> event.getSource().equals(state)
        && event.getMachineRef().equals(SOME_REF)));
    order.verify(successDisposableFacet).dispose();
    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING) &&
        record.getThrown().equals(SOME_DISPOSE_EXCEPTION)));
  }
}
