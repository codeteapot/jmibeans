package com.github.codeteapot.jmibeans.shell.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MachineShellClientCommandExecution<R> {

  void handleOutput(InputStream output) throws IOException;

  void handleError(InputStream error) throws IOException;

  void handleInput(OutputStream input) throws IOException, InterruptedException;

  R mapResult(int exitCode) throws Exception;
}
