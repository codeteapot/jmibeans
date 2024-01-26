package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecution;

class MachineShellCommandExecutionWrapper<R> implements MachineShellClientCommandExecution<R> {

  private final MachineShellCommandExecution<R> wrapped;

  MachineShellCommandExecutionWrapper(MachineShellCommandExecution<R> wrapped) {
    this.wrapped = requireNonNull(wrapped);
  }

  @Override
  public void handleOutput(InputStream output) throws IOException {
    wrapped.handleOutput(output);
  }

  @Override
  public void handleError(InputStream error) throws IOException {
    wrapped.handleError(error);
  }

  @Override
  public void handleInput(OutputStream input) throws IOException, InterruptedException {
    wrapped.handleInput(input);
  }

  @Override
  public R mapResult(int exitCode) throws Exception {
    return wrapped.mapResult(exitCode);
  }
}
