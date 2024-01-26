package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.hamcrest.SomeLogRecordMatcher.someLogRecord;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineBuilderPropertyResolver;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import com.github.codeteapot.jmibeans.profile.MachineBuilderResult;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.profile.MachineDisposeAction;
import com.github.codeteapot.testing.logging.junit.jupiter.LoggingExtension;
import com.github.codeteapot.testing.logging.mockito.MockLogger;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.logging.Handler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
class ManagedMachineTest {

  private static final MachineRef ANY_REF = new MachineRef(new byte[0], new byte[0]);

  private static final Class<Object> ANY_FACET_TYPE = Object.class;

  private static final Class<TestFooMachineFacet> FOO_FACET_TYPE = TestFooMachineFacet.class;
  private static final Class<TestBarMachineFacet> BAR_FACET_TYPE = TestBarMachineFacet.class;

  private static final boolean MAY_INTERRUP_IF_RUNNING = true;

  private static final MachineRef SOME_REF = new MachineRef(new byte[] {1}, new byte[] {3});
  private static final String SOME_REF_STR = "01:03";

  private static final String SOME_BUILDER_PROPERTY_NAME = "someProperty";
  private static final String SOME_BUILDER_PROPERTY_VALUE = "someValue";

  private static final TestFooMachineFacet SOME_FOO_FACET = new TestFooMachineFacet();
  private static final MachineDisposeAction SOME_DISPOSE_ACTION = () -> {
  };

  private static final Exception SOME_EXCEPTION = new Exception();
  private static final MachineBuildingException SOME_BUILDING_EXCEPTION =
      new MachineBuildingException("Some message");


  @Test
  void buildSuccess(
      @Mock ManagedMachineStateChanger someStateChanger,
      @Mock PlatformEventTarget someEventTarget,
      @Mock MachineBuilderPropertyResolver someBuilderPropertyResolver,
      @Mock MachineAgent someAgent,
      @Mock MachineBuilderResult someResult,
      @Mock MachineBuilder someBuilder,
      @Mock ManagedMachineBuildingJob someBuildingJob,
      @Mock Future<Void> anyTask) throws Exception {
    when(someBuilderPropertyResolver.getProperty(SOME_BUILDER_PROPERTY_NAME))
        .thenReturn(singleton(SOME_BUILDER_PROPERTY_VALUE));
    when(someResult.getFacets())
        .thenReturn(singleton(SOME_FOO_FACET));
    doAnswer(invocation -> {
      MachineBuilderContext context = invocation.getArgument(0);
      if (someAgent.equals(context.getAgent()) && context.getProperty(SOME_BUILDER_PROPERTY_NAME)
          .equals(singleton(SOME_BUILDER_PROPERTY_VALUE))) {
        context.addDisposeAction(SOME_DISPOSE_ACTION);
        return someResult;
      }
      return null;
    }).when(someBuilder).build(any());
    when(someBuildingJob.submit(any())).thenAnswer(invocation -> {
      invocation.getArgument(0, ManagedMachineBuildingJobAction.class).build(someBuilder);
      return anyTask;
    });

    new ManagedMachine(changeStateAction -> new ManagedMachineBuildingState(
        someStateChanger,
        SOME_REF,
        someEventTarget,
        someBuilderPropertyResolver,
        someAgent,
        someBuildingJob));

    InOrder order = inOrder(someStateChanger, someEventTarget);
    order.verify(someStateChanger).available(
        SOME_REF,
        someEventTarget,
        singleton(SOME_FOO_FACET),
        singleton(SOME_DISPOSE_ACTION));
    order.verify(someEventTarget).fireAvailable(argThat(event -> SOME_REF.equals(
        event.getMachineRef())));
  }

  @Test
  void buildFailure(
      @Mock ManagedMachineStateChanger someStateChanger,
      @Mock PlatformEventTarget anyEventTarget,
      @Mock MachineBuilderPropertyResolver anyBuilderPropertyResolver,
      @Mock MachineAgent anyAgent,
      @Mock MachineBuilder someBuilder,
      @Mock ManagedMachineBuildingJob someBuildingJob,
      @Mock Future<Void> anyTask,
      @MockLogger(name = "com.github.codeteapot.jmibeans.ManagedMachine") Handler loggerHandler)
      throws Exception {
    doThrow(SOME_BUILDING_EXCEPTION).when(someBuilder).build(any());
    when(someBuildingJob.submit(any())).thenAnswer(invocation -> {
      invocation.getArgument(0, ManagedMachineBuildingJobAction.class).build(someBuilder);
      return anyTask;
    });

    new ManagedMachine(changeStateAction -> new ManagedMachineBuildingState(
        someStateChanger,
        SOME_REF,
        anyEventTarget,
        anyBuilderPropertyResolver,
        anyAgent,
        someBuildingJob));

    InOrder order = inOrder(someStateChanger, loggerHandler);
    order.verify(someStateChanger).buildingFailure(SOME_REF);
    order.verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withThrown(equalTo(SOME_BUILDING_EXCEPTION))));
  }

  @Test
  void failGetFacetWhenBuilding(
      @Mock ManagedMachineStateChanger anyStateChanger,
      @Mock PlatformEventTarget anyEventTarget,
      @Mock MachineBuilderPropertyResolver anyBuilderPropertyResolver,
      @Mock MachineAgent anyAgent,
      @Mock ManagedMachineBuildingJob anyBuildingJob,
      @Mock Future<Void> anyTask) {
    when(anyBuildingJob.submit(any())).thenReturn(anyTask);
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineBuildingState(
            anyStateChanger,
            ANY_REF,
            anyEventTarget,
            anyBuilderPropertyResolver,
            anyAgent,
            anyBuildingJob));

    Throwable e = catchThrowable(() -> machine.getFacet(ANY_FACET_TYPE));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void findFacetWhenAvailable(
      @Mock ManagedMachineStateChanger anyStateChanger,
      @Mock PlatformEventTarget anyEventTarget) {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineAvailableState(
            anyStateChanger,
            ANY_REF,
            anyEventTarget,
            singleton(SOME_FOO_FACET),
            emptySet()));

    Optional<TestFooMachineFacet> fooFacet = machine.getFacet(FOO_FACET_TYPE);

    assertThat(fooFacet).hasValue(SOME_FOO_FACET);
  }

  @Test
  void notFindFacetWhenAvailable(
      @Mock ManagedMachineStateChanger anyStateChanger,
      @Mock PlatformEventTarget anyEventTarget) {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineAvailableState(
            anyStateChanger,
            ANY_REF,
            anyEventTarget,
            singleton(SOME_FOO_FACET),
            emptySet()));

    Optional<TestBarMachineFacet> barFacet = machine.getFacet(BAR_FACET_TYPE);

    assertThat(barFacet).isEmpty();
  }

  @Test
  void failGetFacetWhenBuildingFailure(@Mock ManagedMachineStateChanger anyStateChanger) {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineBuildingFailureState(anyStateChanger, ANY_REF));

    Throwable e = catchThrowable(() -> machine.getFacet(ANY_FACET_TYPE));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void failGetFacetWhenDisposed(@Mock ManagedMachineStateChanger anyStateChanger) {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineDisposedState(anyStateChanger, ANY_REF));

    Throwable e = catchThrowable(() -> machine.getFacet(ANY_FACET_TYPE));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void buildingIsNotReady(
      @Mock ManagedMachineStateChanger anyStateChanger,
      @Mock PlatformEventTarget anyEventTarget,
      @Mock MachineBuilderPropertyResolver anyBuilderPropertyResolver,
      @Mock MachineAgent anyAgent,
      @Mock ManagedMachineBuildingJob anyBuildingJob,
      @Mock Future<Void> anyTask) {
    when(anyBuildingJob.submit(any())).thenReturn(anyTask);
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineBuildingState(
            anyStateChanger,
            ANY_REF,
            anyEventTarget,
            anyBuilderPropertyResolver,
            anyAgent,
            anyBuildingJob));

    boolean ready = machine.isReady();

    assertThat(ready).isFalse();
  }

  @Test
  void availableIsReady(
      @Mock ManagedMachineStateChanger anyStateChanger,
      @Mock PlatformEventTarget anyEventTarget) {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineAvailableState(
            anyStateChanger,
            ANY_REF,
            anyEventTarget,
            emptySet(),
            emptySet()));

    boolean ready = machine.isReady();

    assertThat(ready).isTrue();
  }

  @Test
  void buildingFailureIsNotReady(@Mock ManagedMachineStateChanger anyStateChanger) {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineBuildingFailureState(anyStateChanger, ANY_REF));

    boolean ready = machine.isReady();

    assertThat(ready).isFalse();
  }

  @Test
  void disposedIsNotReady(@Mock ManagedMachineStateChanger anyStateChanger) {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineDisposedState(anyStateChanger, ANY_REF));

    boolean ready = machine.isReady();

    assertThat(ready).isFalse();
  }

  @Test
  void disposeWhenBuilding(
      @Mock ManagedMachineStateChanger someStateChanger,
      @Mock PlatformEventTarget anyEventTarget,
      @Mock MachineBuilderPropertyResolver anyBuilderPropertyResolver,
      @Mock MachineAgent anyAgent,
      @Mock ManagedMachineBuildingJob someBuildingJob,
      @Mock Future<Void> someTask) {
    when(someBuildingJob.submit(any())).thenReturn(someTask);
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineBuildingState(
            someStateChanger,
            SOME_REF,
            anyEventTarget,
            anyBuilderPropertyResolver,
            anyAgent,
            someBuildingJob));

    machine.dispose();

    InOrder order = inOrder(someStateChanger, someTask);
    order.verify(someStateChanger).disposed(SOME_REF);
    order.verify(someTask).cancel(MAY_INTERRUP_IF_RUNNING);
  }

  @Test
  void disposeSuccessWhenAvailable(
      @Mock ManagedMachineStateChanger someStateChanger,
      @Mock PlatformEventTarget someEventTarget,
      @Mock MachineDisposeAction someDisposeAction) throws Exception {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineAvailableState(
            someStateChanger,
            SOME_REF,
            someEventTarget,
            emptySet(),
            singleton(someDisposeAction)));

    machine.dispose();

    InOrder order = inOrder(someStateChanger, someEventTarget, someDisposeAction);
    order.verify(someStateChanger).disposed(SOME_REF);
    order.verify(someEventTarget).fireLost(argThat(event -> SOME_REF.equals(
        event.getMachineRef())));
    order.verify(someDisposeAction).dispose();
  }

  @Test
  void disposeFailureWhenAvailable(
      @Mock ManagedMachineStateChanger someStateChanger,
      @Mock PlatformEventTarget someEventTarget,
      @Mock MachineDisposeAction someDisposeAction,
      @MockLogger(name = "com.github.codeteapot.jmibeans.ManagedMachine") Handler loggerHandler)
      throws Exception {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineAvailableState(
            someStateChanger,
            SOME_REF,
            someEventTarget,
            emptySet(),
            singleton(someDisposeAction)));
    doThrow(SOME_EXCEPTION).when(someDisposeAction).dispose();

    machine.dispose();

    InOrder order = inOrder(someStateChanger, someEventTarget, loggerHandler);
    order.verify(someStateChanger).disposed(SOME_REF);
    order.verify(someEventTarget).fireLost(argThat(event -> SOME_REF.equals(
        event.getMachineRef())));
    order.verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withThrown(equalTo(SOME_EXCEPTION))));
  }

  @Test
  void disposeWhenBuildingFailure(
      @Mock ManagedMachineStateChanger anyStateChanger,
      @MockLogger(name = "com.github.codeteapot.jmibeans.ManagedMachine") Handler loggerHandler)
      throws Exception {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineBuildingFailureState(anyStateChanger, SOME_REF));

    machine.dispose();

    verify(loggerHandler).publish(argThat(someLogRecord()
        .withMessage(containsString(SOME_REF_STR))));
  }

  @Test
  void disposeWhenDisposed(@Mock ManagedMachineStateChanger anyStateChanger) throws Exception {
    ManagedMachine machine = new ManagedMachine(
        changeStateAction -> new ManagedMachineDisposedState(anyStateChanger, SOME_REF));

    Throwable e = catchThrowable(() -> machine.dispose());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }
}
