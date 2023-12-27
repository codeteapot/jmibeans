package com.github.codeteapot.jmibeans.testing.session;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Context in which the execution of a command is emulated.
 *
 * @see MachineTerminalBehavior#execute(MachineTerminalBehaviorContext)
 */
public interface MachineTerminalBehaviorContext {

  /**
   * Gives access to the standard output.
   *
   * @return The standard output when executing the command.
   */
  OutputStream getOutputStream();

  /**
   * Gives access to the error output.
   *
   * @return The error output when executing the command.
   */
  OutputStream getErrorStream();

  /**
   * Gives access to the standard input.
   *
   * @return The standard input when executing the command.
   */
  InputStream getInputStream();
}
