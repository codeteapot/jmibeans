package com.github.codeteapot.jmibeans.shell;

import static java.util.Objects.requireNonNull;

import java.nio.charset.Charset;
import java.util.function.Function;

class DefinedMachineShellCommand<R> implements MachineShellCommand<R> {

  private final String statement;
  private final Function<Charset, MachineShellCommandExecution<R>> executionMapper;

  DefinedMachineShellCommand(
      String statement,
      Function<Charset, MachineShellCommandExecution<R>> executionMapper) {
    this.statement = requireNonNull(statement);
    this.executionMapper = requireNonNull(executionMapper);
  }

  @Override
  public String getStatement() {
    return statement;
  }

  @Override
  public MachineShellCommandExecution<R> getExecution(Charset charset) {
    return executionMapper.apply(charset);
  }
}
