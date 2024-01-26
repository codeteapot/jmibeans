package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class FileExistsCommandExecution implements MachineShellCommandExecution<Boolean> {

  FileExistsCommandExecution() {}

  @Override
  public void handleOutput(InputStream output) throws IOException {}

  @Override
  public void handleError(InputStream error) throws IOException {}

  @Override
  public void handleInput(OutputStream input) throws IOException, InterruptedException {}

  @Override
  public Boolean mapResult(int exitCode) throws Exception {
    return exitCode == 0;
  }
}
