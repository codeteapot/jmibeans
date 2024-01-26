package com.github.codeteapot.jmibeans.testing.session;

/**
 * Defines the behavior of executing a command in a terminal.
 *
 * @see MachineTerminalGiven#behave(MachineTerminalBehavior)
 */
@FunctionalInterface
public interface MachineTerminalBehavior {

  /**
   * Emulates the execution of the command.
   *
   * @param context Emulated execution context.
   *
   * @return The status code resulting from the execution.
   */
  int execute(MachineTerminalBehaviorContext context);
}
