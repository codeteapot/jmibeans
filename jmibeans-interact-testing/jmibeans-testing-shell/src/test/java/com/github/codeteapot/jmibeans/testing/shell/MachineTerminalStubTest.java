package com.github.codeteapot.jmibeans.testing.shell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;

@ExtendWith(MockitoExtension.class)
class MachineTerminalStubTest {

  private static final boolean MATCHING = true;
  private static final boolean NOT_MATCHING = false;

  private static final String SOME_STATEMENT = "some --statement";

  private static final MachineTerminalExecutionResult<Object> SOME_RESULT =
      new MachineTerminalExecutionResult<>();
  private static final IOException SOME_IO_EXCEPTION = new IOException();

  @Mock
  private MachineTerminalExecutor executor;

  @Mock
  private Predicate<String> statementMatcher;

  @Mock
  private MachineTerminalBehavior behavior;

  private MachineTerminalStub stub;

  @BeforeEach
  void setUp() {
    stub = new MachineTerminalStub(executor, statementMatcher, behavior);
  }

  @Test
  void statenentMatches() {
    when(statementMatcher.test(SOME_STATEMENT)).thenReturn(MATCHING);

    boolean result = stub.match(SOME_STATEMENT);

    assertThat(result).isTrue();
  }

  @Test
  void statementDoesNotMatch() {
    when(statementMatcher.test(SOME_STATEMENT)).thenReturn(NOT_MATCHING);

    boolean result = stub.match(SOME_STATEMENT);

    assertThat(result).isFalse();
  }

  @Test
  void executeSuccessfully(@Mock MachineShellCommandExecution<Object> someExecution)
      throws Exception {
    when(executor.execute(eq(behavior), eq(someExecution), anyLong(), any()))
        .thenReturn(SOME_RESULT);

    MachineTerminalExecutionResult<Object> result = stub.execute(someExecution);

    assertThat(result).isEqualTo(SOME_RESULT);
  }

  @Test
  void failWithStreamError(@Mock MachineShellCommandExecution<?> someExecution)
      throws Exception {
    when(executor.execute(eq(behavior), eq(someExecution), anyLong(), any()))
        .thenThrow(SOME_IO_EXCEPTION);

    Throwable e = catchThrowable(() -> stub.execute(someExecution));

    assertThat(e)
        .isInstanceOf(UncheckedIOException.class)
        .hasCause(SOME_IO_EXCEPTION);
  }
}
