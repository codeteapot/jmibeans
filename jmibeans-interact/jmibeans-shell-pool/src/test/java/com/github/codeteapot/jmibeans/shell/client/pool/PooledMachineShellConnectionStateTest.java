package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;

@ExtendWith(MockitoExtension.class)
class PooledMachineShellConnectionStateTest {

  private static final MachineShellClientException SOME_CLIENT_EXCEPTION =
      new MachineShellClientException(new Exception());

  @Mock
  private PooledMachineShellClientConnectionStateChanger stateChanger;

  @Mock
  private PooledMachineShellClientConnectionClosedStateConstructor closedStateConstructor;

  @Mock
  private PooledMachineShellClientConnectionErrorOccurredStateConstructor errStateConstructor;

  private PooledMachineShellClientConnectionState state;

  @BeforeEach
  void setUp() {
    state = new PooledMachineShellClientConnectionAnyState(
        stateChanger,
        closedStateConstructor,
        errStateConstructor);
  }

  @Test
  void onClosed(@Mock PooledMachineShellClientConnectionClosedState closedState) {
    when(closedStateConstructor.construct(
        eq(stateChanger),
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent())))
            .thenReturn(closedState);

    state.onClosed();

    verify(stateChanger).changeState(closedState);
  }

  @Test
  void onClosedWithException(@Mock PooledMachineShellClientConnectionClosedState closedState) {
    when(closedStateConstructor.construct(
        eq(stateChanger),
        argThat(exceptionSupplier -> exceptionSupplier.get()
            .equals(Optional.of(SOME_CLIENT_EXCEPTION))))).thenReturn(closedState);

    state.onClosed(SOME_CLIENT_EXCEPTION);

    verify(stateChanger).changeState(closedState);
  }

  @Test
  void onErrorOccurred(@Mock PooledMachineShellClientConnectionErrorOccurredState errorState) {
    when(errStateConstructor.construct(
        eq(stateChanger),
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent())))
            .thenReturn(errorState);

    state.onErrorOccurred();

    verify(stateChanger).changeState(errorState);
  }

  @Test
  void onErrorOccurredWithException(
      @Mock PooledMachineShellClientConnectionErrorOccurredState errorState) {
    when(errStateConstructor.construct(
        eq(stateChanger),
        argThat(exceptionSupplier -> exceptionSupplier.get()
            .equals(Optional.of(SOME_CLIENT_EXCEPTION))))).thenReturn(errorState);

    state.onErrorOccurred(SOME_CLIENT_EXCEPTION);

    verify(stateChanger).changeState(errorState);
  }
}
