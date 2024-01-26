package com.github.codeteapot.jmibeans.testing.session;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.github.codeteapot.jmibeans.session.MachineCommandExecution;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Predicate;

class MachineTerminalStub {

  private static final long DEFAULT_TIMEOUT_MILLIS = 20000L;

  private final MachineTerminalExecutor executor;
  private final Predicate<String> sentenceMatcher;
  private final MachineTerminalBehavior behavior;

  MachineTerminalStub(
      MachineTerminalExecutor executor,
      Predicate<String> sentenceMatcher,
      MachineTerminalBehavior behavior) {
    this.executor = requireNonNull(executor);
    this.sentenceMatcher = requireNonNull(sentenceMatcher);
    this.behavior = requireNonNull(behavior);
  }

  boolean match(String sentence) {
    return sentenceMatcher.test(sentence);
  }

  <R> MachineTerminalExecutionResult<R> execute(MachineCommandExecution<R> execution) {
    try {
      return executor.execute(behavior, execution, DEFAULT_TIMEOUT_MILLIS, MILLISECONDS);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
