package com.github.codeteapot.jmibeans.testing.shell;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.function.Predicate;

class MachineTerminalGivenStatement implements MachineTerminalGiven {

  private final MachineTerminalExecutor executor;
  private final Set<MachineTerminalStub> stubs;
  private final Predicate<String> statementMatcher;
  private final MachineTerminalStubConstructor stubConstructor;

  MachineTerminalGivenStatement(
      MachineTerminalExecutor executor,
      Set<MachineTerminalStub> stubs,
      Predicate<String> statementMatcher,
      MachineTerminalStubConstructor stubConstructor) {
    this.executor = requireNonNull(executor);
    this.stubs = requireNonNull(stubs);
    this.statementMatcher = requireNonNull(statementMatcher);
    this.stubConstructor = requireNonNull(stubConstructor);
  }

  @Override
  public void behave(MachineTerminalBehavior behavior) {
    stubs.add(stubConstructor.construct(executor, statementMatcher, behavior));
  }
}
