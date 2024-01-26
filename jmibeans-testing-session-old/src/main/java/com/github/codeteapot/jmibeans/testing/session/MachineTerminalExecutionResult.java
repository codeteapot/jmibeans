package com.github.codeteapot.jmibeans.testing.session;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.session.MachineCommandExecutionException;

class MachineTerminalExecutionResult<R> {

  private final R value;
  private final MachineCommandExecutionException executionException;
  private final InterruptedException interruptedException;

  MachineTerminalExecutionResult() {
    value = null;
    executionException = null;
    interruptedException = null;
  }

  MachineTerminalExecutionResult(R value) {
    this.value = value;
    executionException = null;
    interruptedException = null;
  }

  MachineTerminalExecutionResult(MachineCommandExecutionException executionException) {
    value = null;
    this.executionException = requireNonNull(executionException);
    interruptedException = null;
  }

  MachineTerminalExecutionResult(InterruptedException interruptedException) {
    value = null;
    executionException = null;
    this.interruptedException = requireNonNull(interruptedException);
  }

  MachineCommandExecutionResult<R> getValue() throws InterruptedException {
    if (interruptedException != null) {
      throw interruptedException;
    }
    return new MachineTerminalCommandExecutionResult<>(value, executionException);
  }
}
