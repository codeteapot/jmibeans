package com.github.codeteapot.jmibeans.session;

import java.io.Closeable;

/**
 * Point of interaction for a user with the system of a platform machine.
 */
public interface MachineSession extends Closeable {

  /**
   * Executes the specified command and returns the resulting value.
   *
   * @param <R> Return value type.
   *
   * @param command Command to execute.
   *
   * @return The resulting value.
   *
   * @throws MachineSessionException In case there is a problem when establishing the session.
   * @throws MachineCommandExecutionException In case there is any problem when executing the
   *         command.
   */
  <R> R execute(MachineCommand<R> command)
      throws MachineSessionException, MachineCommandExecutionException;

  /**
   * Access the file with the path specified in the platform machine system.
   *
   * <p>The file does not have to exist.
   *
   * @param path The path of the file in the machine system.
   *
   * @return Access to the file.
   *
   * @throws MachineSessionException In case there is a problem when establishing the session.
   */
  MachineSessionFile file(String path) throws MachineSessionException;
}
