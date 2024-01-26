package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellConnectionStateChangerTest {

  @Mock
  private Consumer<MachineShellConnectionState> changeStateAction;

  @Mock
  private MachineShellConnectionClosedStateConstructor closedStateConstructor;

  private MachineShellConnectionStateChanger stateChanger;

  @BeforeEach
  void setUp() {
    stateChanger = new MachineShellConnectionStateChanger(
        changeStateAction,
        closedStateConstructor);
  }

  @Test
  void closed(@Mock MachineShellConnectionClosedState someClosedState) {
    when(closedStateConstructor.construct(stateChanger)).thenReturn(someClosedState);

    stateChanger.closed();

    verify(changeStateAction).accept(someClosedState);
  }
}
