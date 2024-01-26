package com.github.codeteapot.jmibeans.testing.session;

import static java.lang.Integer.parseInt;

import com.github.codeteapot.jmibeans.session.MachineCommandExecution;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

class TestDivisionMachineCommandExecution implements MachineCommandExecution<Integer> {

  private static final int ERROR_BUF_SIZE = 1024;

  private final int divisor;
  private Integer result;
  private StringWriter errorWriter;

  TestDivisionMachineCommandExecution(int divisor) {
    this.divisor = divisor;
    result = null;
    errorWriter = new StringWriter();
  }

  @Override
  public void handleOutput(InputStream output) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(output))) {
      result = parseInt(reader.readLine());
    }
  }

  @Override
  public void handleError(InputStream error) throws IOException {
    try (Reader reader = new InputStreamReader(error)) {
      char[] buf = new char[ERROR_BUF_SIZE];
      int len = reader.read(buf, 0, ERROR_BUF_SIZE);
      while (len > 0) {
        errorWriter.write(buf, 0, len);
        len = reader.read(buf, 0, ERROR_BUF_SIZE);
      }
    }
  }

  @Override
  public void handleInput(OutputStream input) throws IOException, InterruptedException {
    try (PrintWriter writer = new PrintWriter(input)) {
      writer.println(divisor);
    }
  }

  @Override
  public Integer mapResult(int exitCode) throws Exception {
    if (exitCode == 0) {
      return result;
    }
    throw new Exception(errorWriter.toString());
  }
}
