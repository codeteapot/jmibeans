package com.github.codeteapot.jmibeans;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.profile.MachineDisposeAction;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManagedMachineStateChangerTest {

  private static final MachineRef SOME_REF = new MachineRef(new byte[] {1}, new byte[] {3});

  private static final Set<Object> SOME_FACETS = new HashSet<>();
  private static final Set<MachineDisposeAction> SOME_DISPOSE_ACTIONS = new HashSet<>();

  @Mock
  private Consumer<ManagedMachineState> changeStateAction;

  @Mock
  private ManagedMachineAvailableStateConstructor availableStateConstructor;

  @Mock
  private ManagedMachineBuildingFailureStateConstructor buildingFailureStateConstructor;

  @Mock
  private ManagedMachineDisposedStateConstructor disposedStateConstructor;

  private ManagedMachineStateChanger stateChanger;

  @BeforeEach
  void setUp() {
    stateChanger = new ManagedMachineStateChanger(
        changeStateAction,
        availableStateConstructor,
        buildingFailureStateConstructor,
        disposedStateConstructor);
  }

  @Test
  void available(
      @Mock PlatformEventTarget someEventTarget,
      @Mock ManagedMachineAvailableState someAvailableState) {
    when(availableStateConstructor.construct(
        stateChanger,
        SOME_REF,
        someEventTarget,
        SOME_FACETS, SOME_DISPOSE_ACTIONS)).thenReturn(someAvailableState);

    stateChanger.available(SOME_REF, someEventTarget, SOME_FACETS, SOME_DISPOSE_ACTIONS);

    verify(changeStateAction).accept(someAvailableState);
  }

  @Test
  void buildingFailure(@Mock ManagedMachineBuildingFailureState someBuildingFailureState) {
    when(buildingFailureStateConstructor.construct(
        stateChanger,
        SOME_REF)).thenReturn(someBuildingFailureState);

    stateChanger.buildingFailure(SOME_REF);

    verify(changeStateAction).accept(someBuildingFailureState);
  }

  @Test
  void disposed(@Mock ManagedMachineDisposedState someDisposedState) {
    when(disposedStateConstructor.construct(
        stateChanger,
        SOME_REF)).thenReturn(someDisposedState);

    stateChanger.disposed(SOME_REF);

    verify(changeStateAction).accept(someDisposedState);
  }
}
