package com.github.codeteapot.jmibeans.testing.shell;

import java.util.Optional;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;

public interface MachineShellCommandExecutionResult<R> {

  public Optional<R> getValue() throws MachineShellCommandExecutionException;
}
