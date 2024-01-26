package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

@ExtendWith(MockitoExtension.class)
class PooledMachineShellConnectionAcquiredStateTest {

  private static final String ANY_USERNAME = "any";

  private static final String SOME_USERNAME = "scott";
  private static final String SOME_FILE_PATH = "/some/file";

  private static final Object SOME_EXECUTION_RESULT = new Object();

  @Mock
  private PooledMachineShellClientConnectionStateChanger stateChanger;

  @Mock
  private MachineShellClientConnectionBridge bridge;

  @Mock
  private PooledMachineShellClientConnectionAvailableStateConstructor availStateConstructor;

  @Mock
  private PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor;

  private PooledMachineShellClientConnectionAcquiredState state;

  @BeforeEach
  void setUp() {
    state = new PooledMachineShellClientConnectionAcquiredState(
        stateChanger,
        bridge,
        SOME_USERNAME,
        availStateConstructor,
        closingStateConstructor);
  }

  @Test
  void cannotRequest() {
    Throwable e = catchThrowable(() -> state.request(ANY_USERNAME));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotAcquire() {
    Throwable e = catchThrowable(() -> state.acquire());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void executeCommandThroughBridge(@Mock MachineShellClientCommand<Object> someCommand)
      throws Exception {
    when(bridge.execute(someCommand)).thenReturn(SOME_EXECUTION_RESULT);

    Object result = state.execute(someCommand);

    assertThat(result).isEqualTo(SOME_EXECUTION_RESULT);
  }

  @Test
  void accessFileThroughBridge(@Mock MachineShellClientFile someFile) throws Exception {
    when(bridge.file(SOME_FILE_PATH)).thenReturn(someFile);

    MachineShellClientFile file = state.file(SOME_FILE_PATH);

    assertThat(file).isEqualTo(someFile);
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
  void releaseAndClose(
      @Mock MachineShellClientConnectionBridgeCloseTask someCloseTask,
      @Mock PooledMachineShellClientConnectionAvailableState availableState,
      @Mock PooledMachineShellClientConnectionClosingState closingState) throws Exception {
    AtomicReference<Callable<?>> closeTaskAction = new AtomicReference<>();
    when(bridge.closeIdleTimeout(any())).thenAnswer(invocation -> {
      closeTaskAction.set(invocation.getArgument(0));
      return someCloseTask;
    });
    when(availStateConstructor.construct(stateChanger, bridge, SOME_USERNAME, someCloseTask))
        .thenReturn(availableState);
    when(closingStateConstructor.construct(stateChanger))
        .thenReturn(closingState);

    state.release();
    verify(stateChanger).changeState(availableState);

    reset(stateChanger);
    closeTaskAction.get().call();
    verify(stateChanger).changeState(closingState);
  }
}
