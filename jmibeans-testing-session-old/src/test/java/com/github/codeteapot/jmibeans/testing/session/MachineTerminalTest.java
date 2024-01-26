package com.github.codeteapot.jmibeans.testing.session;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.session.MachineCommand;
import com.github.codeteapot.jmibeans.session.MachineCommandExecution;
import com.github.codeteapot.jmibeans.testing.session.MachineCommandExecutionResult;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminal;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalBehavior;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalCommandExecutionResult;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalExecutionResult;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalExecutor;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalStub;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalStubConstructor;
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

@ExtendWith(MockitoExtension.class)
public class MachineTerminalTest {

  private static final boolean MATCHING = true;
  private static final boolean NOT_MATCHING = false;

  private static final Charset SOME_CHARSET = defaultCharset();

  private static final Predicate<String> SOME_SENTENCE_MATCHER = isEqual("some-sentence");
  private static final String SOME_SENTENCE = "some-sentence";

  private static final MachineCommandExecutionResult<Object> SOME_EXECUTION_RESULT =
      new MachineTerminalCommandExecutionResult<Object>(null, null);
  private static final IOException SOME_IO_EXCEPTION = new IOException();

  @Mock
  private MachineTerminalExecutor executor;

  private Set<MachineTerminalStub> stubs;

  @Mock
  private MachineTerminalStubConstructor stubConstructor;

  private MachineTerminal terminal;

  @BeforeEach
  public void setUp() {
    stubs = new HashSet<>();
    terminal = new MachineTerminal(executor, stubs, SOME_CHARSET, stubConstructor);
  }

  @Test
  public void addStub(
      @Mock MachineTerminalStub someStub,
      @Mock MachineTerminalBehavior someBehavior) {
    when(stubConstructor.construct(executor, SOME_SENTENCE_MATCHER, someBehavior))
        .thenReturn(someStub);

    terminal.given(SOME_SENTENCE_MATCHER).behave(someBehavior);

    assertThat(stubs).containsExactly(someStub);
  }

  @Test
  public void executionResultWhenSentenceMatches(
      @Mock MachineTerminalStub someStub,
      @Mock MachineCommand<Object> someCommand,
      @Mock MachineCommandExecution<Object> someCommandExecution,
      @Mock MachineTerminalExecutionResult<Object> someTerminalExecutionResult) throws Exception {
    when(someStub.match(SOME_SENTENCE))
        .thenReturn(MATCHING);
    when(someStub.execute(someCommandExecution))
        .thenReturn(someTerminalExecutionResult);
    when(someCommand.getSentence())
        .thenReturn(SOME_SENTENCE);
    when(someCommand.getExecution(SOME_CHARSET))
        .thenReturn(someCommandExecution);
    when(someTerminalExecutionResult.getValue())
        .thenReturn(SOME_EXECUTION_RESULT);
    stubs.add(someStub);

    MachineCommandExecutionResult<Object> executionResult = terminal.execute(someCommand);

    assertThat(executionResult).isEqualTo(SOME_EXECUTION_RESULT);
  }

  @Test
  public void emptyResultWhenSentenceDoesNotMatch(
      @Mock MachineTerminalStub someStub,
      @Mock MachineCommand<Object> someCommand) throws Exception {
    when(someStub.match(SOME_SENTENCE))
        .thenReturn(NOT_MATCHING);
    when(someCommand.getSentence())
        .thenReturn(SOME_SENTENCE);
    stubs.add(someStub);

    MachineCommandExecutionResult<Object> executionResult = terminal.execute(someCommand);

    assertThat(executionResult.getValue()).isEmpty();
  }

  @Test
  public void failOnExecutionInputOutputError(
      @Mock MachineTerminalStub someStub,
      @Mock MachineCommand<Object> someCommand,
      @Mock MachineCommandExecution<Object> someCommandExecution) throws Exception {
    when(someStub.match(SOME_SENTENCE))
        .thenReturn(MATCHING);
    when(someStub.execute(someCommandExecution))
        .thenThrow(new UncheckedIOException(SOME_IO_EXCEPTION));
    when(someCommand.getSentence())
        .thenReturn(SOME_SENTENCE);
    when(someCommand.getExecution(SOME_CHARSET))
        .thenReturn(someCommandExecution);
    stubs.add(someStub);

    Throwable e = catchThrowable(() -> terminal.execute(someCommand));

    assertThat(e).isEqualTo(SOME_IO_EXCEPTION);
  }
}
