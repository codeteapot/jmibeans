package com.github.codeteapot.jmibeans.testing.session;

import com.github.codeteapot.jmibeans.session.MachineCommandExecutionException;
import com.github.codeteapot.jmibeans.session.MachineSession;
import java.util.Optional;

/**
 * Result of executing a command in the terminal.
 *
 * <p>The getValue() operation returns an empty value if the command sentence does not match any
 * stub.
 *
 * @param <R> The type of value returned by the command.
 *
 * @see MachineTerminal#execute(com.github.codeteapot.jmibeans.session.MachineCommand)
 */
public interface MachineCommandExecutionResult<R> {

  /**
   * Gets the value returned by executing the command.
   *
   * <p>It is empty if the command statement has not matched any stub.
   *
   * @return The value returned by the command or empty if there is no match.
   *
   * @throws MachineCommandExecutionException Propagation of the exception thrown by the execution
   *         of the command.
   *
   * @see MachineSession#execute(com.github.codeteapot.jmibeans.session.MachineCommand)
   */
  public Optional<R> getValue() throws MachineCommandExecutionException;
}
