package com.github.codeteapot.jmibeans.testing.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.session.MachineCommandExecution;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalBehavior;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalExecutionResult;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalExecutor;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalStub;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MachineTerminalStubTest {

  private static final boolean MATCHING = true;
  private static final boolean NOT_MATCHING = false;

  private static final String SOME_SENTENCE = "some-sentence";

  private static final MachineTerminalExecutionResult<Object> SOME_RESULT =
      new MachineTerminalExecutionResult<>();
  private static final IOException SOME_IO_EXCEPTION = new IOException();

  @Mock
  private MachineTerminalExecutor executor;

  @Mock
  private Predicate<String> sentenceMatcher;

  @Mock
  private MachineTerminalBehavior behavior;

  private MachineTerminalStub stub;

  @BeforeEach
  public void setUp() {
    stub = new MachineTerminalStub(executor, sentenceMatcher, behavior);
  }

  @Test
  public void sentenceMatches() {
    when(sentenceMatcher.test(SOME_SENTENCE)).thenReturn(MATCHING);

    boolean result = stub.match(SOME_SENTENCE);

    assertThat(result).isTrue();
  }

  @Test
  public void sentenceDoesNotMatch() {
    when(sentenceMatcher.test(SOME_SENTENCE)).thenReturn(NOT_MATCHING);

    boolean result = stub.match(SOME_SENTENCE);

    assertThat(result).isFalse();
  }

  @Test
  public void executeSuccessfully(@Mock MachineCommandExecution<Object> someExecution)
      throws Exception {
    when(executor.execute(eq(behavior), eq(someExecution), anyLong(), any()))
        .thenReturn(SOME_RESULT);

    MachineTerminalExecutionResult<Object> result = stub.execute(someExecution);

    assertThat(result).isEqualTo(SOME_RESULT);
  }

  @Test
  public void failWithStreamError(@Mock MachineCommandExecution<?> someExecution)
      throws Exception {
    when(executor.execute(eq(behavior), eq(someExecution), anyLong(), any()))
        .thenThrow(SOME_IO_EXCEPTION);

    Throwable e = catchThrowable(() -> stub.execute(someExecution));

    assertThat(e)
        .isInstanceOf(UncheckedIOException.class)
        .hasCause(SOME_IO_EXCEPTION);
  }
}
