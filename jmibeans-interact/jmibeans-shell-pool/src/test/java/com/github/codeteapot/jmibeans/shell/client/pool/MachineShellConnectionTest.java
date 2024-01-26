package com.github.codeteapot.jmibeans.shell.client.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellFile;

@ExtendWith(MockitoExtension.class)
class MachineShellConnectionTest {

  private static final String SOME_FILE_PATH = "/some/file";

  private static final Object SOME_EXECUTION_RESULT = new Object();

  @Mock
  private MachineShellConnectionState state;

  private MachineShellConnection connection;

  @BeforeEach
  void setUp(@Mock MachineShellConnectionStateMapper stateMapper) {
    when(stateMapper.map(any())).thenReturn(state);
    connection = new MachineShellConnectionImpl(stateMapper);
  }

  @Test
  void executeCommand(@Mock MachineShellCommand<Object> someCommand) throws Exception {
    when(state.execute(someCommand)).thenReturn(SOME_EXECUTION_RESULT);

    Object result = connection.execute(someCommand);

    assertThat(result).isEqualTo(SOME_EXECUTION_RESULT);
  }

  @Test
  void accessFile(@Mock MachineShellFile someFile) throws Exception {
    when(state.file(SOME_FILE_PATH)).thenReturn(someFile);

    MachineShellFile file = connection.file(SOME_FILE_PATH);

    assertThat(file).isEqualTo(someFile);
  }

  @Test
  void closeIt() throws Exception {
    connection.close();

    verify(state).close();
  }
}
