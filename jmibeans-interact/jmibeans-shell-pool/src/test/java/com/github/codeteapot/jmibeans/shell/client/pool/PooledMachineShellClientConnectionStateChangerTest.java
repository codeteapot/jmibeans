package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PooledMachineShellClientConnectionStateChangerTest {

  private static final String SOME_USERNAME = "scott";

  @Mock
  private Consumer<PooledMachineShellClientConnectionState> changeStateAction;

  @Mock
  private PooledMachineShellClientConnectionAcquiredStateConstructor acquiredStateConstructor;

  @Mock
  private PooledMachineShellClientConnectionAvailableStateConstructor availStateConstructor;

  @Mock
  private PooledMachineShellClientConnectionClosedStateConstructor closedStateConstructor;

  @Mock
  private PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor;

  @Mock
  private PooledMachineShellClientConnectionErrorOccurredStateConstructor errStateConstructor;

  @Mock
  private PooledMachineShellClientConnectionRequestedStateConstructor reqStateConstructor;

  private PooledMachineShellClientConnectionStateChanger stateChanger;

  @BeforeEach
  void setUp() {
    stateChanger = new PooledMachineShellClientConnectionStateChanger(
        changeStateAction,
        acquiredStateConstructor,
        availStateConstructor,
        closedStateConstructor,
        closingStateConstructor,
        errStateConstructor,
        reqStateConstructor);
  }

  @Test
  void acquired(
      @Mock MachineShellClientConnectionBridge someBridge,
      @Mock PooledMachineShellClientConnectionAcquiredState someAcquiredState) {
    when(acquiredStateConstructor.construct(
        stateChanger,
        someBridge,
        SOME_USERNAME)).thenReturn(someAcquiredState);

    stateChanger.acquired(someBridge, SOME_USERNAME);

    verify(changeStateAction).accept(someAcquiredState);
  }

  @Test
  void available(
      @Mock MachineShellClientConnectionBridge someBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask someCloseTask,
      @Mock PooledMachineShellClientConnectionAvailableState someAvailableState) {
    when(availStateConstructor.construct(
        stateChanger,
        someBridge,
        SOME_USERNAME,
        someCloseTask)).thenReturn(someAvailableState);

    stateChanger.available(someBridge, SOME_USERNAME, someCloseTask);

    verify(changeStateAction).accept(someAvailableState);
  }

  @Test
  void closed(
      @Mock Supplier<Optional<Exception>> someExceptionSupplier,
      @Mock PooledMachineShellClientConnectionClosedState someClosedState) {
    when(closedStateConstructor.construct(
        stateChanger,
        someExceptionSupplier)).thenReturn(someClosedState);

    stateChanger.closed(someExceptionSupplier);

    verify(changeStateAction).accept(someClosedState);
  }

  @Test
  void closing(@Mock PooledMachineShellClientConnectionClosingState someClosingState) {
    when(closingStateConstructor.construct(stateChanger)).thenReturn(someClosingState);

    stateChanger.closing();

    verify(changeStateAction).accept(someClosingState);
  }

  @Test
  void errorOccurred(
      @Mock Supplier<Optional<Exception>> someExceptionSupplier,
      @Mock PooledMachineShellClientConnectionErrorOccurredState someErrorOccurredState) {
    when(errStateConstructor.construct(
        stateChanger,
        someExceptionSupplier)).thenReturn(someErrorOccurredState);

    stateChanger.errorOccurred(someExceptionSupplier);

    verify(changeStateAction).accept(someErrorOccurredState);
  }

  @Test
  void requested(
      @Mock MachineShellClientConnectionBridge someBridge,
      @Mock PooledMachineShellClientConnectionRequestedState someRequestedState) {
    when(reqStateConstructor.construct(
        stateChanger,
        someBridge,
        SOME_USERNAME)).thenReturn(someRequestedState);

    stateChanger.requested(someBridge, SOME_USERNAME);

    verify(changeStateAction).accept(someRequestedState);
  }
}
