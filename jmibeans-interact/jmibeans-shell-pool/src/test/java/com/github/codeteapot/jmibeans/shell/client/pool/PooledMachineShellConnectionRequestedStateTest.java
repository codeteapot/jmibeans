package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;

@ExtendWith(MockitoExtension.class)
class PooledMachineShellConnectionRequestedStateTest {

  private static final String ANY_USERNAME = "any";
  private static final String ANY_FILE_PATH = "/any/file";

  private static final String SOME_USERNAME = "scott";

  @Mock
  private PooledMachineShellClientConnectionStateChanger stateChanger;

  @Mock
  private MachineShellClientConnectionBridge bridge;

  @Mock
  private PooledMachineShellClientConnectionAcquiredStateConstructor acquiredStateConstructor;

  @Mock
  private PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor;

  private PooledMachineShellClientConnectionRequestedState state;

  @BeforeEach
  void setUp() {
    state = new PooledMachineShellClientConnectionRequestedState(
        stateChanger,
        bridge,
        SOME_USERNAME,
        acquiredStateConstructor,
        closingStateConstructor);
  }

  @Test
  void requestNotAvailable() {
    boolean available = state.request(ANY_USERNAME);

    assertThat(available).isFalse();
  }

  @Test
  void acquireIt(@Mock PooledMachineShellClientConnectionAcquiredState someAcquiredState) {
    when(acquiredStateConstructor.construct(stateChanger, bridge, SOME_USERNAME))
        .thenReturn(someAcquiredState);

    state.acquire();

    verify(stateChanger).changeState(someAcquiredState);
  }

  @Test
  void failWhenExecutingCommand(@Mock MachineShellClientCommand<?> anyCommand) {
    Throwable e = catchThrowable(() -> state.execute(anyCommand));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void failWhenAccessingFile() {
    Throwable e = catchThrowable(() -> state.file(ANY_FILE_PATH));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void closeNowThroughBridge(
      @Mock PooledMachineShellClientConnectionClosingState closingState) {
    when(closingStateConstructor.construct(stateChanger)).thenReturn(closingState);

    state.closeNow();

    InOrder order = inOrder(stateChanger, bridge);
    order.verify(stateChanger).changeState(closingState);
    order.verify(bridge).closeNow();
  }

  @Test
  void cannotRelease() {
    Throwable e = catchThrowable(() -> state.release());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }
}
