package com.github.codeteapot.jmibeans.shell.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MachineShellClientExceptionTest {

  private static final String ANY_MESSAGE = "any-message";
  private static final Throwable ANY_CAUSE = new Exception();

  private static final String SOME_MESSAGE = "some-message";
  private static final Throwable SOME_CAUSE = new Exception();

  @Test
  void hasMessageOnly() {
    MachineShellClientException exception = new MachineShellClientException(SOME_MESSAGE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  void hasCauseOnly() {
    MachineShellClientException exception = new MachineShellClientException(SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }

  @Test
  void hasMessage() {
    MachineShellClientException exception = new MachineShellClientException(
        SOME_MESSAGE,
        ANY_CAUSE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  void hasCause() {
    MachineShellClientException exception = new MachineShellClientException(
        ANY_MESSAGE,
        SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }
}