package com.github.codeteapot.jmibeans.testing.session;

import java.util.function.Predicate;

@FunctionalInterface
interface MachineTerminalStubConstructor {

  MachineTerminalStub construct(
      MachineTerminalExecutor executor,
      Predicate<String> sentenceMatcher,
      MachineTerminalBehavior behavior);
}
