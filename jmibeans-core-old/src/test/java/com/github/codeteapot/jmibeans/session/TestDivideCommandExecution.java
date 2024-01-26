package com.github.codeteapot.jmibeans.session;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.session.MachineCommandExecution;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

class TestDivideCommandExecution implements MachineCommandExecution<Integer> {

  private static final int BUFFER_SIZE = 64;
  private static final long HANDLE_END_DELAY_MILLIS = 1000L;

  private final Charset charset;
  private final int dividend;
  private final int divisor;
  private Integer result;
  private String errorMessage;

  TestDivideCommandExecution(Charset charset, int dividend, int divisor) {
    this.charset = requireNonNull(charset);
    this.dividend = dividend;
    this.divisor = divisor;
    result = null;
    errorMessage = null;
  }

  @Override
  public void handleOutput(InputStream output) throws IOException {
    try (ByteArrayOutputStream response = new ByteArrayOutputStream()) {
      byte[] b = new byte[BUFFER_SIZE];
      int len = output.read(b, 0, BUFFER_SIZE);
      while (len > 0) {
        response.write(b, 0, len);
        len = output.read(b, 0, BUFFER_SIZE);
      }
      result = parseInt(new String(response.toByteArray(), charset));
    }
    try {
      sleep(HANDLE_END_DELAY_MILLIS);
    } catch (InterruptedException e) {
      currentThread().interrupt();
    }
  }

  @Override
  public void handleError(InputStream error) throws IOException {
    try (ByteArrayOutputStream response = new ByteArrayOutputStream()) {
      byte[] b = new byte[BUFFER_SIZE];
      int len = error.read(b, 0, BUFFER_SIZE);
      while (len > 0) {
        response.write(b, 0, len);
        len = error.read(b, 0, BUFFER_SIZE);
      }
      errorMessage = new String(response.toByteArray(), charset);
    }
    try {
      sleep(HANDLE_END_DELAY_MILLIS);
    } catch (InterruptedException e) {
      currentThread().interrupt();
    }
  }

  @Override
  public void handleInput(OutputStream input) throws IOException, InterruptedException {
    input.write(String.valueOf(dividend).getBytes());
    input.write('\n');
    input.flush();
    input.write(String.valueOf(divisor).getBytes());
    input.write('\n');
    input.flush();
  }

  @Override
  public Integer mapResult(int exitCode) throws Exception {
    if (exitCode != 0) {
      throw new Exception(errorMessage);
    }
    return result;
  }
}
