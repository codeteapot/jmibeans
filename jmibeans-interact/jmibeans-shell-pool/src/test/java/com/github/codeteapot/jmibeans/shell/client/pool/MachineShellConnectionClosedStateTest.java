package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellException;

@ExtendWith(MockitoExtension.class)
class MachineShellConnectionClosedStateTest {

  private static final String ANY_FILE_PATH = "/any/file";

  private MachineShellConnectionClosedState state;

  @BeforeEach
  void setUp(@Mock MachineShellConnectionStateChanger stateChanger) {
    state = new MachineShellConnectionClosedState(stateChanger);
  }

  @Test
  void failWhenExecutingCommand(@Mock MachineShellCommand<?> anyCommand) {
    Throwable e = catchThrowable(() -> state.execute(anyCommand));

    assertThat(e).isInstanceOf(MachineShellException.class);
  }

  @Test
  void failWhenAccessingFile() {
    Throwable e = catchThrowable(() -> state.file(ANY_FILE_PATH));

    assertThat(e).isInstanceOf(MachineShellException.class);
  }

  @Test
  void failWhenClosing() {
    Throwable e = catchThrowable(() -> state.close());

    assertThat(e).isInstanceOf(Exception.class);
  }
}
