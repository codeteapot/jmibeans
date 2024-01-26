package com.github.codeteapot.jmibeans.testing.shell;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;

public class MachineTerminal {

  private final MachineTerminalExecutor executor;
  private final Set<MachineTerminalStub> stubs;
  private final Charset charset;
  private final MachineTerminalStubConstructor stubConstructor;

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

  public MachineTerminalGiven given(Predicate<String> statementMatcher) {
    return new MachineTerminalGivenStatement(
        executor,
        stubs,
        statementMatcher,
        stubConstructor);
  }

  public <R> MachineShellCommandExecutionResult<R> execute(MachineShellCommand<R> command)
      throws IOException, InterruptedException {
    try {
      return stubs.stream()
          .filter(stub -> stub.match(command.getStatement()))
          .findAny()
          .map(stub -> stub.execute(command.getExecution(charset)))
          .orElseGet(MachineTerminalExecutionResult::new)
          .getValue();
    } catch (UncheckedIOException e) {
      throw e.getCause();
    }
  }
}
