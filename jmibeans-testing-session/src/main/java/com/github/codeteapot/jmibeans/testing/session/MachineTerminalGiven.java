package com.github.codeteapot.jmibeans.testing.session;

/**
 * Stub definition step in which the behavior given a statement matcher is defined.
 *
 * @see MachineTerminal#given(java.util.function.Predicate)
 */
public interface MachineTerminalGiven {

  /**
   * Defines the behavior given a statement matcher.
   *
   * @param behavior The behavior associated with the statement matcher.
   */
  void behave(MachineTerminalBehavior behavior);
}
