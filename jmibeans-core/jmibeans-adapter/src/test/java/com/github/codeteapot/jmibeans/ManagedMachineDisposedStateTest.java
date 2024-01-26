package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

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
class ManagedMachineDisposedStateTest {

  private static final MachineRef SOME_REF = new MachineRef(new byte[] {1}, new byte[] {3});

  @Mock
  private ManagedMachineStateChanger stateChanger;

  private ManagedMachineDisposedState state;

  @BeforeEach
  void setUp() {
    state = new ManagedMachineDisposedState(stateChanger, SOME_REF);
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
  void cannotDispose() {
    Throwable e = catchThrowable(() -> state.dispose());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }
}
