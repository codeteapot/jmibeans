package com.github.codeteapot.jmibeans.testing.session;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.session.MachineCommand;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Terminal for simulating command execution.
 *
 * <p>For example. Let {@code EchoMachineCommand} be an implementation of {@link MachineCommand}
 * that executes {@code "echo &gt;single-arg&lt;"}, where {@code "single-arg"} is passed as a
 * constructor parameter, and returns the result from standard output as string. The following code
 * verifies that the implementation works as expected.
 *
 * <pre>
 * terminal.given(isEqual("echo $HOME")).behave(context -&gt; {
 *   try (PrintWriter writer = new PrintWriter(context.getOutputStream())) {
 *     writer.println("/home/scott");
 *     return 0;
 *   }
 * });
 * assert terminal.execute(new EchoMachineCommand("$HOME"))
 *   .getValue()
 *   .map("/home/scott"::equals)
 *   .orElse(false);
 * </pre>
 * 
 * <p>Note that this implementation is also responsible for discarding the line break caused by the
 * execution of {@code "echo"} on standard output.
 */
public class MachineTerminal {

  private final MachineTerminalExecutor executor;
  private final Set<MachineTerminalStub> stubs;
  private final Charset charset;
  private final MachineTerminalStubConstructor stubConstructor;

  /**
   * Create a terminal with default settings.
   */
  public MachineTerminal() {
    this(
        new MachineTerminalExecutor(),
        new HashSet<>(),
        defaultCharset(),
        MachineTerminalStub::new);
  }

  MachineTerminal(
      MachineTerminalExecutor executor,
      Set<MachineTerminalStub> stubs,
      Charset charset,
      MachineTerminalStubConstructor stubConstructor) {
    this.executor = requireNonNull(executor);
    this.stubs = requireNonNull(stubs);
    this.charset = requireNonNull(charset);
    this.stubConstructor = requireNonNull(stubConstructor);
  }

  /**
   * Starts the definition of a stub with the specified sentence matcher.
   *
   * @param sentenceMatcher The sentence matcher to which a behavior will be assigned.
   *
   * @return The definition step that allows the behavior to be assigned.
   */
  public MachineTerminalGiven given(Predicate<String> sentenceMatcher) {
    return new MachineTerminalGivenSentence(
        executor,
        stubs,
        sentenceMatcher,
        stubConstructor);
  }

  /**
   * Try running a command to verify that its behavior is as expected.
   *
   * <p>This is a blocking operation.
   *
   * @param <R> Return type of the command to execute.
   *
   * @param command Command to be checked.
   *
   * @return The result of the possible execution of the command.
   *
   * @throws IOException In case of an I/O error.
   * @throws InterruptedException If the thread in which the command is executed is interrupted.
   */
  public <R> MachineCommandExecutionResult<R> execute(MachineCommand<R> command)
      throws IOException, InterruptedException {
    try {
      return stubs.stream()
          .filter(stub -> stub.match(command.getSentence()))
          .findAny()
          .map(stub -> stub.execute(command.getExecution(charset)))
          .orElseGet(MachineTerminalExecutionResult::new)
          .getValue();
    } catch (UncheckedIOException e) {
      throw e.getCause();
    }
  }
}
