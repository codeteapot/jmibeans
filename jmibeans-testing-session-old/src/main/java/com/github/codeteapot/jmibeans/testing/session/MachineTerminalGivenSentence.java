package com.github.codeteapot.jmibeans.testing.session;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.function.Predicate;

class MachineTerminalGivenSentence implements MachineTerminalGiven {

  private final MachineTerminalExecutor executor;
  private final Set<MachineTerminalStub> stubs;
  private final Predicate<String> sentenceMatcher;
  private final MachineTerminalStubConstructor stubConstructor;

  MachineTerminalGivenSentence(
      MachineTerminalExecutor executor,
      Set<MachineTerminalStub> stubs,
      Predicate<String> sentenceMatcher,
      MachineTerminalStubConstructor stubConstructor) {
    this.executor = requireNonNull(executor);
    this.stubs = requireNonNull(stubs);
    this.sentenceMatcher = requireNonNull(sentenceMatcher);
    this.stubConstructor = requireNonNull(stubConstructor);
  }

  @Override
  public void behave(MachineTerminalBehavior behavior) {
    stubs.add(stubConstructor.construct(executor, sentenceMatcher, behavior));
  }
}
