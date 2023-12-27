package com.github.codeteapot.jmibeans.port;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.port.MachineSessionHostResolutionException;
import org.junit.jupiter.api.Test;

public class MachineSessionHostResolutionExceptionTest {

  private static final String ANY_MESSAGE = "any-message";
  private static final Throwable ANY_CAUSE = new Exception();

  private static final String SOME_MESSAGE = "some-message";
  private static final Throwable SOME_CAUSE = new Exception();


  @Test
  public void hasMessageOnly() {
    MachineSessionHostResolutionException exception = new MachineSessionHostResolutionException(
        SOME_MESSAGE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  public void hasCauseOnly() {
    MachineSessionHostResolutionException exception = new MachineSessionHostResolutionException(
        SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }

  @Test
  public void hasMessage() {
    MachineSessionHostResolutionException exception = new MachineSessionHostResolutionException(
        SOME_MESSAGE,
        ANY_CAUSE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  public void hasCause() {
    MachineSessionHostResolutionException exception = new MachineSessionHostResolutionException(
        ANY_MESSAGE,
        SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }
}
