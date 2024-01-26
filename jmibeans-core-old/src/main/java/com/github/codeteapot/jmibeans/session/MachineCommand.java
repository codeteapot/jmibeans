package com.github.codeteapot.jmibeans.session;

import java.nio.charset.Charset;

/**
 * Command to be executed remotely on a platform machine.
 *
 * <p>It may be dependent on the mechanism used to interact with the operating system of the
 * machine.
 *
 * <p>The methods of this interface will be called during the execution of the
 * {@link MachineSession#execute(MachineCommand)} method to determine what to execute and how to
 * treat the response from the operating system to obtain the return value.
 *
 * @param <R> The type to which the result of executing the command is mapped.
 *
 * @see MachineCommandExecution
 */
public interface MachineCommand<R> {

  /**
   * Statement received by the command interpreter of the operating system.
   *
   * @return The statement in a string, which can include newlines and other control characters.
   */
  String getSentence();

  /**
   * Execution control instance for the command.
   *
   * @param charset The encoding used in the response from the operating system.
   *
   * @return The execution control instance.
   */
  MachineCommandExecution<R> getExecution(Charset charset);
}
