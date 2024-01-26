package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.inOrder;
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
class PooledMachineShellConnectionAvailableStateTest {

  private static final String ANY_FILE_PATH = "/any/file";
  
  private static final String SOME_USERNAME = "scott";

  private static final String ANOTHER_USERNAME = "jessie";

  @Mock
  private PooledMachineShellClientConnectionStateChanger stateChanger;

  @Mock
  private MachineShellClientConnectionBridge bridge;

  @Mock
  private MachineShellClientConnectionBridgeCloseTask closeTask;

  @Mock
  private PooledMachineShellClientConnectionRequestedStateConstructor reqStateConstructor;
  
  @Mock
  private PooledMachineShellClientConnectionClosingStateConstructor closingStateConstructor;

  private PooledMachineShellClientConnectionAvailableState state;

  @BeforeEach
  void setUp() {
    state = new PooledMachineShellClientConnectionAvailableState(
        stateChanger,
        bridge,
        SOME_USERNAME,
        closeTask,
        reqStateConstructor,
        closingStateConstructor);
  }

  @Test
  void requestMatchingUsername(
      @Mock PooledMachineShellClientConnectionRequestedState requestedState) {
    when(reqStateConstructor.construct(stateChanger, bridge, SOME_USERNAME))
        .thenReturn(requestedState);

    boolean available = state.request(SOME_USERNAME);

    assertThat(available).isTrue();
    InOrder order = inOrder(closeTask, stateChanger);
    order.verify(closeTask).cancel();
    order.verify(stateChanger).changeState(requestedState);
  }

  @Test
  void requestUnmatchingUsername(
      @Mock PooledMachineShellClientConnectionRequestedState requestedState) {
    boolean available = state.request(ANOTHER_USERNAME);

    assertThat(available).isFalse();
  }
  
  @Test
  void cannotAcquire() {
    Throwable e = catchThrowable(() -> state.acquire());

    assertThat(e).isInstanceOf(IllegalStateException.class);
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
