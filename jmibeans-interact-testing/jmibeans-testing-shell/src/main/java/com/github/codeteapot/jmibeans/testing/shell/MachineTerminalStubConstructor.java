package com.github.codeteapot.jmibeans.testing.shell;

import java.util.function.Predicate;

@FunctionalInterface
interface MachineTerminalStubConstructor {

  MachineTerminalStub construct(
      MachineTerminalExecutor executor,
      Predicate<String> statementMatcher,
      MachineTerminalBehavior behavior);
}
