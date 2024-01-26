package com.github.codeteapot.jmibeans.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MachineBuildingExceptionTest {

  private static final String ANY_MESSAGE = "any-message";
  private static final Throwable ANY_CAUSE = new Exception();

  private static final String SOME_MESSAGE = "some-message";
  private static final Throwable SOME_CAUSE = new Exception();

  @Test
  void hasMessageOnly() {
    MachineBuildingException exception = new MachineBuildingException(SOME_MESSAGE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  void hasCauseOnly() {
    MachineBuildingException exception = new MachineBuildingException(SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }

  @Test
  void hasMessage() {
    MachineBuildingException exception = new MachineBuildingException(SOME_MESSAGE, ANY_CAUSE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  void hasCause() {
    MachineBuildingException exception = new MachineBuildingException(ANY_MESSAGE, SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }
}
