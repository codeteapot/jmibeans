package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.MachineShellFile;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecution;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellConnectionTest {

  private static final String ANY_FILE_PATH = "/any/file";

  private static final String SOME_FILE_PATH = "/some/file";

  private static final String SOME_COMMAND_STATEMENT = "some --statement";
  private static final Charset SOME_COMMAND_CHARSET = UTF_8;
  private static final int SOME_EXIT_CODE = 1;

  private static final Object SOME_EXECUTION_RESULT = new Object();

  private static final MachineShellClientException SOME_CLIENT_EXCEPTION =
      new MachineShellClientException(new Exception());
  private static final MachineShellClientCommandExecutionException SOME_CLIENT_EXECUTION_EXCEPTION =
      new MachineShellClientCommandExecutionException(new Exception());

  @Test
  void availableExecuteCommand(
      @Mock MachineShellConnectionStateChanger anyStateChanger,
      @Mock PooledMachineShellClientConnection somePooled,
      @Mock MachineShellCommand<Object> someCommand,
      @Mock MachineShellCommandExecution<Object> someCommandExecution,
      @Mock InputStream someOutput,
      @Mock InputStream someError,
      @Mock OutputStream someInput) throws Exception {
    @SuppressWarnings("resource")
    MachineShellConnection connection = new MachineShellConnectionImpl(
        changeStateAction -> new MachineShellConnectionActiveState(anyStateChanger, somePooled));
    when(someCommand.getStatement())
        .thenReturn(SOME_COMMAND_STATEMENT);
    when(someCommand.getExecution(SOME_COMMAND_CHARSET))
        .thenReturn(someCommandExecution);
    when(someCommandExecution.mapResult(SOME_EXIT_CODE))
        .thenReturn(SOME_EXECUTION_RESULT);
    when(somePooled.execute(argThat(command -> SOME_COMMAND_STATEMENT.equals(
        command.getStatement())))).thenAnswer(invocation -> {
          MachineShellClientCommandExecution<?> execution = invocation.getArgument(
              0,
              MachineShellClientCommand.class).getExecution(SOME_COMMAND_CHARSET);
          execution.handleOutput(someOutput);
          execution.handleError(someError);
          execution.handleInput(someInput);
          return execution.mapResult(SOME_EXIT_CODE);
        });

    Object result = connection.execute(someCommand);

    verify(someCommandExecution).handleOutput(someOutput);
    verify(someCommandExecution).handleError(someError);
    verify(someCommandExecution).handleInput(someInput);
    assertThat(result).isEqualTo(SOME_EXECUTION_RESULT);
  }

  @Test
  void failWhenAvailableExecutingCommandWithError(
      @Mock MachineShellConnectionStateChanger anyStateChanger,
      @Mock PooledMachineShellClientConnection somePooled,
      @Mock MachineShellCommand<Object> someCommand) throws Exception {
    @SuppressWarnings("resource")
    MachineShellConnection connection = new MachineShellConnectionImpl(
        changeStateAction -> new MachineShellConnectionActiveState(anyStateChanger, somePooled));
    when(someCommand.getStatement())
        .thenReturn(SOME_COMMAND_STATEMENT);
    when(somePooled.execute(argThat(command -> command.getStatement().equals(
        SOME_COMMAND_STATEMENT)))).thenThrow(SOME_CLIENT_EXCEPTION);

    Throwable e = catchThrowable(() -> connection.execute(someCommand));

    assertThat(e)
        .isInstanceOf(MachineShellException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void failWhenAvailableExecutingCommandWithExecutionError(
      @Mock MachineShellConnectionStateChanger anyStateChanger,
      @Mock PooledMachineShellClientConnection somePooled,
      @Mock MachineShellCommand<Object> someCommand) throws Exception {
    @SuppressWarnings("resource")
    MachineShellConnection connection = new MachineShellConnectionImpl(
        changeStateAction -> new MachineShellConnectionActiveState(anyStateChanger, somePooled));
    when(someCommand.getStatement())
        .thenReturn(SOME_COMMAND_STATEMENT);
    when(somePooled.execute(argThat(command -> command.getStatement().equals(
        SOME_COMMAND_STATEMENT)))).thenThrow(SOME_CLIENT_EXECUTION_EXCEPTION);

    Throwable e = catchThrowable(() -> connection.execute(someCommand));

    assertThat(e)
        .isInstanceOf(MachineShellCommandExecutionException.class)
        .hasCause(SOME_CLIENT_EXECUTION_EXCEPTION);
  }

  @Test
  void availableAccessFile(
      @Mock MachineShellConnectionStateChanger anyStateChanger,
      @Mock PooledMachineShellClientConnection somePooled,
      @Mock MachineShellClientFile someFile,
      @Mock InputStream someInput,
      @Mock OutputStream someOutput) throws Exception {
    @SuppressWarnings("resource")
    MachineShellConnection connection = new MachineShellConnectionImpl(
        changeStateAction -> new MachineShellConnectionActiveState(anyStateChanger, somePooled));
    when(somePooled.file(SOME_FILE_PATH))
        .thenReturn(someFile);
    when(someFile.getInputStream())
        .thenReturn(someInput);
    when(someFile.getOutputStream())
        .thenReturn(someOutput);

    MachineShellFile file = connection.file(SOME_FILE_PATH);
    InputStream input = file.getInputStream();
    OutputStream output = file.getOutputStream();

    assertThat(input).isEqualTo(someInput);
    assertThat(output).isEqualTo(someOutput);
  }

  @Test
  void failAvailableAccessingFile(
      @Mock MachineShellConnectionStateChanger anyStateChanger,
      @Mock PooledMachineShellClientConnection somePooled) throws Exception {
    @SuppressWarnings("resource")
    MachineShellConnection connection = new MachineShellConnectionImpl(
        changeStateAction -> new MachineShellConnectionActiveState(anyStateChanger, somePooled));
    when(somePooled.file(SOME_FILE_PATH))
        .thenThrow(SOME_CLIENT_EXCEPTION);

    Throwable e = catchThrowable(() -> connection.file(SOME_FILE_PATH));

    assertThat(e)
        .isInstanceOf(MachineShellException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void availableClose(
      @Mock MachineShellConnectionStateChanger someStateChanger,
      @Mock PooledMachineShellClientConnection somePooled) throws Exception {
    MachineShellConnection connection = new MachineShellConnectionImpl(
        changeStateAction -> new MachineShellConnectionActiveState(someStateChanger, somePooled));

    connection.close();

    InOrder order = inOrder(someStateChanger, somePooled);
    order.verify(someStateChanger).closed();
    order.verify(somePooled).release();
  }

  @Test
  void failWhenClosedExecutingCommand(
      @Mock MachineShellConnectionStateChanger anyStateChanger,
      @Mock MachineShellCommand<?> anyCommand) throws Exception {
    @SuppressWarnings("resource")
    MachineShellConnection connection = new MachineShellConnectionImpl(
        changeStateAction -> new MachineShellConnectionClosedState(anyStateChanger));

    Throwable e = catchThrowable(() -> connection.execute(anyCommand));

    assertThat(e).isInstanceOf(MachineShellException.class);
  }

  @Test
  void failWhenClosedAccessingFile(
      @Mock MachineShellConnectionStateChanger anyStateChanger) throws Exception {
    @SuppressWarnings("resource")
    MachineShellConnection connection = new MachineShellConnectionImpl(
        changeStateAction -> new MachineShellConnectionClosedState(anyStateChanger));

    Throwable e = catchThrowable(() -> connection.file(ANY_FILE_PATH));

    assertThat(e).isInstanceOf(MachineShellException.class);
  }

  @Test
  void failWhenClosedClosing(
      @Mock MachineShellConnectionStateChanger anyStateChanger) throws Exception {
    MachineShellConnection connection = new MachineShellConnectionImpl(
        changeStateAction -> new MachineShellConnectionClosedState(anyStateChanger));

    Throwable e = catchThrowable(() -> connection.close());

    assertThat(e).isInstanceOf(Exception.class);
  }
}
