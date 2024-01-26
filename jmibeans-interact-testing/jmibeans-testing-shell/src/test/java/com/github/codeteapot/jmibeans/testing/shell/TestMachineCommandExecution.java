package com.github.codeteapot.jmibeans.testing.shell;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;

class TestMachineCommandExecution<R> implements MachineShellCommandExecution<R> {

  private final OutputHandler outputHandler;
  private final ErrorHandler errorHandler;
  private final InputHandler inputHandler;
  private final ResultMapper<R> resultMapper;

  private TestMachineCommandExecution(
      OutputHandler outputHandler,
      ErrorHandler errorHandler,
      InputHandler inputHandler,
      ResultMapper<R> resultMapper) {
    this.outputHandler = requireNonNull(outputHandler);
    this.errorHandler = requireNonNull(errorHandler);
    this.inputHandler = requireNonNull(inputHandler);
    this.resultMapper = requireNonNull(resultMapper);
  }

  @Override
  public void handleOutput(InputStream output) throws IOException {
    outputHandler.handleOutput(output);
  }

  @Override
  public void handleError(InputStream error) throws IOException {
    errorHandler.handleError(error);
  }

  @Override
  public void handleInput(OutputStream input) throws IOException, InterruptedException {
    inputHandler.handleInput(input);
  }

  @Override
  public R mapResult(int exitCode) throws Exception {
    return resultMapper.mapResult(exitCode);
  }

  static <R> MachineShellCommandExecution<R> execution(
      OutputHandler outputHandler,
      ErrorHandler errorHandler,
      InputHandler inputHandler,
      ResultMapper<R> resultMapper) {
    return new TestMachineCommandExecution<>(
        outputHandler,
        errorHandler,
        inputHandler,
        resultMapper);
  }

  @FunctionalInterface
  interface OutputHandler {

    void handleOutput(InputStream output) throws IOException;
  }

  @FunctionalInterface
  interface ErrorHandler {

    void handleError(InputStream error) throws IOException;
  }

  @FunctionalInterface
  interface InputHandler {

    void handleInput(OutputStream input) throws IOException, InterruptedException;
  }

  @FunctionalInterface
  interface ResultMapper<R> {

    R mapResult(int exitCode) throws Exception;
  }
}
