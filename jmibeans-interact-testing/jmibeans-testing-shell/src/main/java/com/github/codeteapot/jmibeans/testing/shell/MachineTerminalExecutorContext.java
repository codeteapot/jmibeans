package com.github.codeteapot.jmibeans.testing.shell;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class MachineTerminalExecutorContext implements MachineTerminalBehaviorContext, Closeable {

  private final OutputStream output;
  private final OutputStream error;
  private final InputStream input;

  MachineTerminalExecutorContext(
      PipedInputStream pipedOutput,
      PipedInputStream pipedError,
      PipedOutputStream pipedInput) throws IOException {
    output = new PipedOutputStream(pipedOutput);
    error = new PipedOutputStream(pipedError);
    input = new PipedInputStream(pipedInput);
  }

  @Override
  public OutputStream getOutputStream() {
    return output;
  }

  @Override
  public OutputStream getErrorStream() {
    return error;
  }

  @Override
  public InputStream getInputStream() {
    return input;
  }

  @Override
  public void close() throws IOException {
    output.close();
    error.close();
    input.close();
  }
}
