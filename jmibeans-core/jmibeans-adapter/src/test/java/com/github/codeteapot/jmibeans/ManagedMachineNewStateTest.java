package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

@ExtendWith(MockitoExtension.class)
class ManagedMachineNewStateTest {

  private static final MachineRef SOME_REF = new MachineRef(new byte[] {1}, new byte[] {3});

  @Mock
  private ManagedMachineStateChanger stateChanger;

  @Mock
  private PlatformEventTarget eventTarget;

  @Mock
  private ManagedMachineBuildingStateConstructor buildingStateConstructor;

  private ManagedMachineNewState state;

  @BeforeEach
  void setUp() {
    state = new ManagedMachineNewState(
        stateChanger,
        SOME_REF,
        eventTarget,
        buildingStateConstructor);
  }

  @Test
  void cannotGetFacet() {
    Throwable e = catchThrowable(() -> state.getFacet(TestFooMachineFacet.class));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void isNotReady() {
    boolean ready = state.isReady();

    assertThat(ready).isFalse();
  }

  @Test
  void buildIt(
      @Mock Consumer<MachineRef> someRemovalAction,
      @Mock ExecutorService someBuilderExecutor,
      @Mock MachineBuilder someBuilder,
      @Mock MachineAgent someAgent,
      @Mock ManagedMachineBuildingState someBuildingState) {
    when(buildingStateConstructor.construct(
        stateChanger,
        SOME_REF,
        someRemovalAction,
        eventTarget,
        someBuilderExecutor,
        someBuilder,
        someAgent)).thenReturn(someBuildingState);

    state.build(someRemovalAction, someBuilderExecutor, someBuilder, someAgent);

    verify(stateChanger).changeState(someBuildingState);
  }

  @Test
  void cannotDispose() {
    Throwable e = catchThrowable(() -> state.dispose());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }
}
