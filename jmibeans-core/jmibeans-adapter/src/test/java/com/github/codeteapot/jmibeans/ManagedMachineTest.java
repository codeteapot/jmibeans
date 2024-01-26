package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
class ManagedMachineTest {

  private static final boolean READY = true;
  private static final boolean NOT_READY = false;

  private static final TestFooMachineFacet SOME_FACET = new TestFooMachineFacet();

  @Mock
  private ManagedMachineState state;

  private ManagedMachine machine;

  @BeforeEach
  void setUp(@Mock ManagedMachineStateMapper stateMapper) {
    when(stateMapper.map(any())).thenReturn(state);
    machine = new ManagedMachine(stateMapper);
  }

  @Test
  void getFacet() {
    when(state.getFacet(TestFooMachineFacet.class)).thenReturn(Optional.of(SOME_FACET));

    Optional<TestFooMachineFacet> facet = machine.getFacet(TestFooMachineFacet.class);

    assertThat(facet).hasValue(SOME_FACET);
  }

  @Test
  void build(
      @Mock Consumer<MachineRef> someRemovalAction,
      @Mock ExecutorService someBuilderExecutor,
      @Mock MachineBuilder someBuilder,
      @Mock MachineAgent someAgent) {
    machine.build(someRemovalAction, someBuilderExecutor, someBuilder, someAgent);

    verify(state).build(someRemovalAction, someBuilderExecutor, someBuilder, someAgent);
  }

  @Test
  void isReady() {
    when(state.isReady()).thenReturn(READY);

    boolean ready = machine.isReady();

    assertThat(ready).isTrue();
  }

  @Test
  void isNotReady() {
    when(state.isReady()).thenReturn(NOT_READY);

    boolean ready = machine.isReady();

    assertThat(ready).isFalse();
  }

  @Test
  void dispose() {
    machine.dispose();

    verify(state).dispose();
  }
}
