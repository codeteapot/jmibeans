package com.github.codeteapot.jmibeans.shell;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MachineShellCommandExecutionExceptionTest {

  private static final String ANY_MESSAGE = "any-message";
  private static final Throwable ANY_CAUSE = new Exception();

  private static final String SOME_MESSAGE = "some-message";
  private static final Throwable SOME_CAUSE = new Exception();

  @Test
  void hasMessageOnly() {
    MachineShellCommandExecutionException exception = new MachineShellCommandExecutionException(
        SOME_MESSAGE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  void hasCauseOnly() {
    MachineShellCommandExecutionException exception = new MachineShellCommandExecutionException(
        SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }

  @Test
  void hasMessage() {
    MachineShellCommandExecutionException exception = new MachineShellCommandExecutionException(
        SOME_MESSAGE,
        ANY_CAUSE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  void hasCause() {
    MachineShellCommandExecutionException exception = new MachineShellCommandExecutionException(
        ANY_MESSAGE,
        SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }
}
