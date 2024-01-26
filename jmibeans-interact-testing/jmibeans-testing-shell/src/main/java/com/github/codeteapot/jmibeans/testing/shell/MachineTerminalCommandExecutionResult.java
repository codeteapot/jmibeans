package com.github.codeteapot.jmibeans.testing.shell;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;

class MachineTerminalCommandExecutionResult<R> implements MachineShellCommandExecutionResult<R> {

  private final R value;
  private final MachineShellCommandExecutionException executionException;

  MachineTerminalCommandExecutionResult(
      R value,
      MachineShellCommandExecutionException executionException) {
    this.value = value;
    this.executionException = executionException;
  }

  @Override
  public Optional<R> getValue() throws MachineShellCommandExecutionException {
    if (executionException != null) {
      throw executionException;
    }
    return ofNullable(value);
  }
}
