package com.github.codeteapot.jmibeans.shell;

import java.nio.charset.Charset;
import java.util.function.Function;

public interface MachineShellCommand<R> {

  String getStatement();

  MachineShellCommandExecution<R> getExecution(Charset charset);

  static <R> MachineShellCommand<R> shellCommand(
      String statement,
      Function<Charset, MachineShellCommandExecution<R>> executionMapper) {
    return new DefinedMachineShellCommand<>(statement, executionMapper);
  }
}
