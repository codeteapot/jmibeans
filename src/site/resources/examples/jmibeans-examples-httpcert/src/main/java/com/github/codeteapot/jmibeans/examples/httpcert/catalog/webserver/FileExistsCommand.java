package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.nio.charset.Charset;

class FileExistsCommand implements MachineShellCommand<Boolean> {

  private final String path;

  FileExistsCommand(String path) {
    this.path = requireNonNull(path);
  }

  @Override
  public String getStatement() {
    return new StringBuilder().append("stat ").append(path).toString();
  }

  @Override
  public MachineShellCommandExecution<Boolean> getExecution(Charset charset) {
    return new FileExistsCommandExecution();
  }
}
