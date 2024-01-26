package com.github.codeteapot.jmibeans.examples.httpcert.catalog;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.nio.charset.Charset;
import java.util.Properties;

class PropertiesLoadCommand implements MachineShellCommand<Properties> {

  private final String path;

  PropertiesLoadCommand(String path) {
    this.path = requireNonNull(path);
  }

  @Override
  public String getStatement() {
    return path;
  }

  @Override
  public MachineShellCommandExecution<Properties> getExecution(Charset charset) {
    return new PropertiesLoadCommandExecution(charset);
  }
}
