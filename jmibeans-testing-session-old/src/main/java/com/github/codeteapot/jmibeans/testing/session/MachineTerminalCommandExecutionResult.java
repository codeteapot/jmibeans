package com.github.codeteapot.jmibeans.testing.session;

import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.session.MachineCommandExecutionException;
import java.util.Optional;

class MachineTerminalCommandExecutionResult<R> implements MachineCommandExecutionResult<R> {

  private final R value;
  private final MachineCommandExecutionException executionException;

  MachineTerminalCommandExecutionResult(
      R value,
      MachineCommandExecutionException executionException) {
    this.value = value;
    this.executionException = executionException;
  }

  public Optional<R> getValue() throws MachineCommandExecutionException {
    if (executionException != null) {
      throw executionException;
    }
    return ofNullable(value);
  }
}
