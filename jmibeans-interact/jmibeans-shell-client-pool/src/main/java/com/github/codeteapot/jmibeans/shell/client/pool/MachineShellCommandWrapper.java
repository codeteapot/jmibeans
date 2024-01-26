package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import java.nio.charset.Charset;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecution;

class MachineShellCommandWrapper<R> implements MachineShellClientCommand<R> {

  private final MachineShellCommand<R> wrapped;

  MachineShellCommandWrapper(MachineShellCommand<R> wrapped) {
    this.wrapped = requireNonNull(wrapped);
  }

  @Override
  public String getStatement() {
    return wrapped.getStatement();
  }

  @Override
  public MachineShellClientCommandExecution<R> getExecution(Charset charset) {
    return new MachineShellCommandExecutionWrapper<>(wrapped.getExecution(charset));
  }
}
