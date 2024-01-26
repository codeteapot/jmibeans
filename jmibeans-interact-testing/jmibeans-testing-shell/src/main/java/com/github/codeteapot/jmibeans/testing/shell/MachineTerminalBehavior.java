package com.github.codeteapot.jmibeans.testing.shell;

@FunctionalInterface
public interface MachineTerminalBehavior {

  int execute(MachineTerminalBehaviorContext context);
}
