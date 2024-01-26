package com.github.codeteapot.jmibeans.examples.httpcert.catalog;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.function.Function;
import java.util.function.Predicate;

public class MachineCommandError {

  private static final int BUFFER_SIZE = 256;

  private final Charset charset;
  private final StringBuilder errorStr;

  public MachineCommandError(Charset charset) {
    this.charset = requireNonNull(charset);
    errorStr = new StringBuilder();
  }

  public void handleError(InputStream error) throws IOException {
    try (Reader reader = new InputStreamReader(error, charset)) {
      char[] buf = new char[BUFFER_SIZE];
      int len = reader.read(buf, 0, BUFFER_SIZE);
      while (len > 0) {
        errorStr.append(buf, 0, len);
        len = reader.read(buf, 0, BUFFER_SIZE);
      }
    }
  }

  public void throwExceptionIf(
      int exitCode,
      Predicate<? super Integer> negatedExitCodeCondition,
      Function<String, Exception> exceptionMapper) throws Exception {
    if (negatedExitCodeCondition.test(exitCode)) {
      throw exceptionMapper.apply(errorStr.toString());
    }
  }
}
