package com.github.codeteapot.jmibeans.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.session.MachineSessionException;
import org.junit.jupiter.api.Test;

public class MachineSessionExceptionTest {

  private static final String ANY_MESSAGE = "any-message";
  private static final Throwable ANY_CAUSE = new Exception();

  private static final String SOME_MESSAGE = "some-message";
  private static final Throwable SOME_CAUSE = new Exception();


  @Test
  public void hasMessageOnly() {
    MachineSessionException exception = new MachineSessionException(SOME_MESSAGE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  public void hasCauseOnly() {
    MachineSessionException exception = new MachineSessionException(SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }

  @Test
  public void hasMessage() {
    MachineSessionException exception = new MachineSessionException(SOME_MESSAGE, ANY_CAUSE);

    String message = exception.getMessage();

    assertThat(message).isEqualTo(SOME_MESSAGE);
  }

  @Test
  public void hasCause() {
    MachineSessionException exception = new MachineSessionException(ANY_MESSAGE, SOME_CAUSE);

    Throwable cause = exception.getCause();

    assertThat(cause).isEqualTo(SOME_CAUSE);
  }
}
