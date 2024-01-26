package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.MachineShellFile;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecution;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

@ExtendWith(MockitoExtension.class)
class MachineShellConnectionActiveStateTest {

  private static final String SOME_FILE_PATH = "/some/file";

  private static final String SOME_COMMAND_STATEMENT = "some --statement";
  private static final Charset SOME_COMMAND_CHARSET = UTF_8;
  private static final int SOME_EXIT_CODE = 1;

  private static final Object SOME_EXECUTION_RESULT = new Object();

  private static final MachineShellClientException SOME_CLIENT_EXCEPTION =
      new MachineShellClientException(new Exception());
  private static final MachineShellClientCommandExecutionException SOME_CLIENT_EXECUTION_EXCEPTION =
      new MachineShellClientCommandExecutionException(new Exception());

  @Mock
  private MachineShellConnectionStateChanger stateChanger;

  @Mock
  private PooledMachineShellClientConnection pooled;

  @Mock
  private MachineShellConnectionClosedStateConstructor closedStateConstructor;

  private MachineShellConnectionActiveState state;

  @BeforeEach
  void setUp() {
    state = new MachineShellConnectionActiveState(stateChanger, pooled, closedStateConstructor);
  }

  @Test
  void executeCommand(
      @Mock MachineShellCommand<Object> someCommand,
      @Mock MachineShellCommandExecution<Object> someCommandExecution,
      @Mock InputStream someOutput,
      @Mock InputStream someError,
      @Mock OutputStream someInput) throws Exception {
    when(someCommand.getStatement())
        .thenReturn(SOME_COMMAND_STATEMENT);
    when(someCommand.getExecution(SOME_COMMAND_CHARSET))
        .thenReturn(someCommandExecution);
    when(someCommandExecution.mapResult(SOME_EXIT_CODE))
        .thenReturn(SOME_EXECUTION_RESULT);
    when(pooled.execute(argThat(command -> command.getStatement().equals(SOME_COMMAND_STATEMENT))))
        .thenAnswer(invocation -> {
          MachineShellClientCommandExecution<?> execution = invocation.getArgument(
              0,
              MachineShellClientCommand.class).getExecution(SOME_COMMAND_CHARSET);
          execution.handleOutput(someOutput);
          execution.handleError(someError);
          execution.handleInput(someInput);
          return execution.mapResult(SOME_EXIT_CODE);
        });

    Object result = state.execute(someCommand);

    verify(someCommandExecution).handleOutput(someOutput);
    verify(someCommandExecution).handleError(someError);
    verify(someCommandExecution).handleInput(someInput);
    assertThat(result).isEqualTo(SOME_EXECUTION_RESULT);
  }

  @Test
  void failWhenExecutingCommandWithError(@Mock MachineShellCommand<Object> someCommand)
      throws Exception {
    when(someCommand.getStatement())
        .thenReturn(SOME_COMMAND_STATEMENT);
    when(pooled.execute(argThat(command -> command.getStatement().equals(SOME_COMMAND_STATEMENT))))
        .thenThrow(SOME_CLIENT_EXCEPTION);

    Throwable e = catchThrowable(() -> state.execute(someCommand));

    assertThat(e)
        .isInstanceOf(MachineShellException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void failWhenExecutingCommandWithExecutionError(@Mock MachineShellCommand<Object> someCommand)
      throws Exception {
    when(someCommand.getStatement())
        .thenReturn(SOME_COMMAND_STATEMENT);
    when(pooled.execute(argThat(command -> command.getStatement().equals(SOME_COMMAND_STATEMENT))))
        .thenThrow(SOME_CLIENT_EXECUTION_EXCEPTION);

    Throwable e = catchThrowable(() -> state.execute(someCommand));

    assertThat(e)
        .isInstanceOf(MachineShellCommandExecutionException.class)
        .hasCause(SOME_CLIENT_EXECUTION_EXCEPTION);
  }

  @Test
  void accessFile(
      @Mock MachineShellClientFile someFile,
      @Mock InputStream someInput,
      @Mock OutputStream someOutput) throws Exception {
    when(pooled.file(SOME_FILE_PATH))
        .thenReturn(someFile);
    when(someFile.getInputStream())
        .thenReturn(someInput);
    when(someFile.getOutputStream())
        .thenReturn(someOutput);

    MachineShellFile file = state.file(SOME_FILE_PATH);
    InputStream input = file.getInputStream();
    OutputStream output = file.getOutputStream();

    assertThat(input).isEqualTo(someInput);
    assertThat(output).isEqualTo(someOutput);
  }

  @Test
  void failAccessingFile() throws Exception {
    when(pooled.file(SOME_FILE_PATH))
        .thenThrow(SOME_CLIENT_EXCEPTION);

    Throwable e = catchThrowable(() -> state.file(SOME_FILE_PATH));

    assertThat(e)
        .isInstanceOf(MachineShellException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void closeIt(@Mock MachineShellConnectionClosedState closedState) throws Exception {
    when(closedStateConstructor.construct(stateChanger)).thenReturn(closedState);

    state.close();

    InOrder order = inOrder(stateChanger, pooled);
    order.verify(stateChanger).changeState(closedState);
    order.verify(pooled).release();
  }
}
