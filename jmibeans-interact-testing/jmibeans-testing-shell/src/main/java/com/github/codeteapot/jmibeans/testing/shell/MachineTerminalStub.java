package com.github.codeteapot.jmibeans.testing.shell;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Predicate;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;

class MachineTerminalStub {

  private static final long DEFAULT_TIMEOUT_MILLIS = 20000L;

  private final MachineTerminalExecutor executor;
  private final Predicate<String> statementMatcher;
  private final MachineTerminalBehavior behavior;

  MachineTerminalStub(
      MachineTerminalExecutor executor,
      Predicate<String> statementMatcher,
      MachineTerminalBehavior behavior) {
    this.executor = requireNonNull(executor);
    this.statementMatcher = requireNonNull(statementMatcher);
    this.behavior = requireNonNull(behavior);
  }

  boolean match(String statement) {
    return statementMatcher.test(statement);
  }

  <R> MachineTerminalExecutionResult<R> execute(MachineShellCommandExecution<R> execution) {
    try {
      return executor.execute(behavior, execution, DEFAULT_TIMEOUT_MILLIS, MILLISECONDS);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
