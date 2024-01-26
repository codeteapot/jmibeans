package com.github.codeteapot.jmibeans.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Control of the execution of a platform machine command.
 *
 * @param <R> The type to which the result of executing the command is mapped.
 *
 * @see MachineCommand#getExecution(java.nio.charset.Charset)
 */
public interface MachineCommandExecution<R> {

  /**
   * Processes the main output to update, if necessary, the state of the execution.
   *
   * <p>This method is called at the time command execution begins, in parallel with
   * {@link #handleError(InputStream)} and {@link #handleInput(OutputStream)}.
   *
   * @param output Main output of the command execution.
   *
   * @throws IOException In case of an input failure during the process.
   */
  void handleOutput(InputStream output) throws IOException;

  /**
   * Processes the error output to update, if necessary, the state of the execution.
   *
   * <p>This method is called at the time command execution begins, in parallel with
   * {@link #handleOutput(InputStream)} and {@link #handleInput(OutputStream)}.
   *
   * @param error Error output of the command execution.
   *
   * @throws IOException In case of an input failure during the process.
   */
  void handleError(InputStream error) throws IOException;

  /**
   * Allows interaction with the machine system through the main input.
   *
   * <p>It can be kept waiting for some condition on the state of the execution to be fulfilled to
   * produce some writing on the main output.
   *
   * <p>This method is called at the time command execution begins, in parallel with
   * {@link #handleOutput(InputStream)} and {@link #handleError(InputStream)}.
   *
   * <p>This method must terminate at least as soon as the call to {@link #mapResult(int)} is made.
   * After that, the effects of writing to the main output should have no effect on the system of
   * the machine.
   *
   * @param input Main output of the command execution.
   *
   * @throws IOException In case of an output failure during the process.
   * @throws InterruptedException  In case some wait is interrupted due to the interruption of the
   *         thread that produces it.
   */
  void handleInput(OutputStream input) throws IOException, InterruptedException;

  /**
   * Returns the resulting instance of the execution state, taking into account the exit code.
   *
   * <p>This method is called when the execution of the command on the system of the machine is
   * complete, but not before the {@link #handleOutput(InputStream)} and
   * {@link #handleError(InputStream)} calls have finished.
   *
   * @param exitCode The exit code returned by the machine system.
   *
   * @return The result of the operation, which will be returned by
   *         {@link MachineSession#execute(MachineCommand)}.
   *
   * @throws Exception If the execution is considered failed.
   */
  R mapResult(int exitCode) throws Exception;
}
