package com.github.codeteapot.jmibeans.examples.httpcert.catalog;

import static java.util.function.Predicate.isEqual;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

class PropertiesLoadCommandExecution implements MachineShellCommandExecution<Properties> {

  private final MachineCommandError commandError;
  private final Properties result;

  PropertiesLoadCommandExecution(Charset charset) {
    this.commandError = new MachineCommandError(charset);
    result = new Properties();
  }

  @Override
  public void handleOutput(InputStream output) throws IOException {
    result.load(output);
  }

  @Override
  public void handleError(InputStream error) throws IOException {
    commandError.handleError(error);
  }

  @Override
  public void handleInput(OutputStream input) throws IOException, InterruptedException {}

  @Override
  public Properties mapResult(int exitCode) throws Exception {
    commandError.throwExceptionIf(exitCode, isEqual(0).negate(), Exception::new);
    return result;
  }

}
