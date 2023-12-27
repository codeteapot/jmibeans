package com.github.codeteapot.jmibeans.testing.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.github.codeteapot.jmibeans.session.MachineCommandExecutionException;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalCommandExecutionResult;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

// TODO Remove it. Related class is covered by MachineTerminalStubTest
@Disabled
public class MachineTerminalCommandExecutionResultTest {

  private static final Object ANY_VALUE = new Object();

  private static final Object NULL_VALUE = null;
  private static final MachineCommandExecutionException NULL_EXECUTION_EXCEPTION = null;

  private static final Object SOME_VALUE = new Object();
  private static final MachineCommandExecutionException SOME_EXECUTION_EXCEPTION =
      new MachineCommandExecutionException("any-message");

  @Test
  public void returningEmptyValue() throws Exception {
    MachineTerminalCommandExecutionResult<?> result = new MachineTerminalCommandExecutionResult<>(
        NULL_VALUE,
        NULL_EXECUTION_EXCEPTION);

    Optional<?> value = result.getValue();

    assertThat(value).isEmpty();
  }

  @Test
  public void returningSomeValue() throws Exception {
    MachineTerminalCommandExecutionResult<Object> result =
        new MachineTerminalCommandExecutionResult<>(SOME_VALUE, NULL_EXECUTION_EXCEPTION);

    Optional<Object> value = result.getValue();

    assertThat(value).hasValue(SOME_VALUE);
  }

  @Test
  public void throwingExecutionException() {
    MachineTerminalCommandExecutionResult<?> result = new MachineTerminalCommandExecutionResult<>(
        ANY_VALUE,
        SOME_EXECUTION_EXCEPTION);

    Throwable e = catchThrowable(() -> result.getValue());

    assertThat(e).isEqualTo(SOME_EXECUTION_EXCEPTION);
  }
}
