package com.github.codeteapot.jmibeans.testing.shell;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;

@ExtendWith(MockitoExtension.class)
class MachineTerminalTest {

  private static final boolean MATCHING = true;
  private static final boolean NOT_MATCHING = false;

  private static final Charset SOME_CHARSET = defaultCharset();

  private static final Predicate<String> SOME_STATEMENT_MATCHER = isEqual("some --statement");
  private static final String SOME_STATEMENT = "some --statement";

  private static final MachineShellCommandExecutionResult<Object> SOME_EXECUTION_RESULT =
      new MachineTerminalCommandExecutionResult<Object>(null, null);
  private static final IOException SOME_IO_EXCEPTION = new IOException();

  @Mock
  private MachineTerminalExecutor executor;

  private Set<MachineTerminalStub> stubs;

  @Mock
  private MachineTerminalStubConstructor stubConstructor;

  private MachineTerminal terminal;

  @BeforeEach
  void setUp() {
    stubs = new HashSet<>();
    terminal = new MachineTerminal(executor, stubs, SOME_CHARSET, stubConstructor);
  }

  @Test
  void addStub(
      @Mock MachineTerminalStub someStub,
      @Mock MachineTerminalBehavior someBehavior) {
    when(stubConstructor.construct(executor, SOME_STATEMENT_MATCHER, someBehavior))
        .thenReturn(someStub);

    terminal.given(SOME_STATEMENT_MATCHER).behave(someBehavior);

    assertThat(stubs).containsExactly(someStub);
  }

  @Test
  void executionResultWhenStatementMatches(
      @Mock MachineTerminalStub someStub,
      @Mock MachineShellCommand<Object> someCommand,
      @Mock MachineShellCommandExecution<Object> someCommandExecution,
      @Mock MachineTerminalExecutionResult<Object> someTerminalExecutionResult) throws Exception {
    when(someStub.match(SOME_STATEMENT))
        .thenReturn(MATCHING);
    when(someStub.execute(someCommandExecution))
        .thenReturn(someTerminalExecutionResult);
    when(someCommand.getStatement())
        .thenReturn(SOME_STATEMENT);
    when(someCommand.getExecution(SOME_CHARSET))
        .thenReturn(someCommandExecution);
    when(someTerminalExecutionResult.getValue())
        .thenReturn(SOME_EXECUTION_RESULT);
    stubs.add(someStub);

    MachineShellCommandExecutionResult<Object> executionResult = terminal.execute(someCommand);

    assertThat(executionResult).isEqualTo(SOME_EXECUTION_RESULT);
  }

  @Test
  void emptyResultWhenStatementDoesNotMatch(
      @Mock MachineTerminalStub someStub,
      @Mock MachineShellCommand<Object> someCommand) throws Exception {
    when(someStub.match(SOME_STATEMENT))
        .thenReturn(NOT_MATCHING);
    when(someCommand.getStatement())
        .thenReturn(SOME_STATEMENT);
    stubs.add(someStub);

    MachineShellCommandExecutionResult<Object> executionResult = terminal.execute(someCommand);

    assertThat(executionResult.getValue()).isEmpty();
  }

  @Test
  void failOnExecutionInputOutputError(
      @Mock MachineTerminalStub someStub,
      @Mock MachineShellCommand<Object> someCommand,
      @Mock MachineShellCommandExecution<Object> someCommandExecution) throws Exception {
    when(someStub.match(SOME_STATEMENT))
        .thenReturn(MATCHING);
    when(someStub.execute(someCommandExecution))
        .thenThrow(new UncheckedIOException(SOME_IO_EXCEPTION));
    when(someCommand.getStatement())
        .thenReturn(SOME_STATEMENT);
    when(someCommand.getExecution(SOME_CHARSET))
        .thenReturn(someCommandExecution);
    stubs.add(someStub);

    Throwable e = catchThrowable(() -> terminal.execute(someCommand));

    assertThat(e).isEqualTo(SOME_IO_EXCEPTION);
  }
}
