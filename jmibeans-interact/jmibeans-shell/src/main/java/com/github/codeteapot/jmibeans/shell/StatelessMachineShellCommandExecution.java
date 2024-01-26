package com.github.codeteapot.jmibeans.shell;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class StatelessMachineShellCommandExecution<O, E, R>
    implements MachineShellCommandExecution<R> {

  private static final int BUFFER_SIZE = 256;

  private final Charset charset;
  private final StreamFunction<O> outputMapper;
  private final StreamFunction<E> errorMapper;
  private final StreamSupplier inputSupplier;
  private final ResultFunction<O, E, R> resultCombiner;
  private final AtomicReference<O> outputResult;
  private final AtomicReference<E> errorResult;

  public StatelessMachineShellCommandExecution(
      Charset charset,
      StreamFunction<O> outputMapper,
      StreamFunction<E> errorMapper,
      StreamSupplier inputSupplier,
      ResultFunction<O, E, R> resultCombiner) {
    this.charset = requireNonNull(charset);
    this.outputMapper = requireNonNull(outputMapper);
    this.errorMapper = requireNonNull(errorMapper);
    this.inputSupplier = requireNonNull(inputSupplier);
    this.resultCombiner = requireNonNull(resultCombiner);
    outputResult = new AtomicReference<>();
    errorResult = new AtomicReference<>();
  }

  @Override
  public void handleOutput(InputStream output) throws IOException {
    outputResult.set(outputMapper.apply(charset, output));
  }

  @Override
  public void handleError(InputStream error) throws IOException {
    errorResult.set(errorMapper.apply(charset, error));
  }

  @Override
  public void handleInput(OutputStream input) throws IOException, InterruptedException {
    inputSupplier.get(charset, input);
  }

  @Override
  public R mapResult(int exitCode) throws Exception {
    return resultCombiner.apply(charset, outputResult.get(), errorResult.get(), exitCode);
  }

  public static <O, E, R> Function< //
      Charset, //
      MachineShellCommandExecution<R>> statelessShellCommandExecution(
          StreamFunction<O> outputMapper,
          StreamFunction<E> errorMapper,
          StreamSupplier inputSupplier,
          ResultFunction<O, E, R> resultCombiner) {
    return charset -> new StatelessMachineShellCommandExecution<>(
        charset,
        outputMapper,
        errorMapper,
        inputSupplier,
        resultCombiner);
  }

  public static StreamFunction<Void> ignoreOutput() {
    return (charset, output) -> null;
  }

  public static StreamFunction<byte[]> readOutputByteArray() {
    return (charset, output) -> {
      try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
        byte[] buf = new byte[BUFFER_SIZE];
        int len = output.read(buf, 0, BUFFER_SIZE);
        while (len > 0) {
          bytes.write(buf, 0, len);
          len = output.read(buf, 0, BUFFER_SIZE);
        }
        return bytes.toByteArray();
      }
    };
  }

  public static StreamFunction<String> readOutputString() {
    return (charset, error) -> {
      try (Reader reader = new InputStreamReader(error, charset)) {
        StringBuilder outputStr = new StringBuilder();
        char[] buf = new char[BUFFER_SIZE];
        int len = reader.read(buf, 0, BUFFER_SIZE);
        while (len > 0) {
          outputStr.append(buf, 0, len);
          len = reader.read(buf, 0, BUFFER_SIZE);
        }
        return outputStr.toString();
      }
    };
  }

  public static StreamSupplier withoutInput() {
    return (charset, input) -> {
    };
  }

  public static StreamSupplier writeInput(Callable<byte[]> bytes) {
    return (charset, input) -> {
      try {
        input.write(bytes.call());
      } catch (Exception e) {
        throw new IOException(e);
      }
    };
  }

  public static StreamSupplier closeInput() {
    return (charset, input) -> input.close();
  }

  public static <O, E, R> ResultFunction<O, E, R> returnNull() {
    return (charset, outputResult, errorResult, exitCode) -> null;
  }

  public static <O, E> ResultFunction<O, E, O> returnOutput() {
    return (charset, outputResult, errorResult, exitCode) -> outputResult;
  }

  public static <O, E> ResultFunction<O, E, Boolean> returnExitCodeTest(
      Predicate<? super Integer> exitCodeCondition) {
    return (charset, outputResult, errorResult, exitCode) -> exitCodeCondition.test(exitCode);
  }

  public static <O, E extends Exception, R> ResultFunction<O, E, R> throwExceptionIf(
      Predicate<? super Integer> exitCodeCondition,
      ResultFunction<O, E, R> elseMapper) {
    return (charset, outputResult, errorResult, exitCode) -> {
      if (exitCodeCondition.test(exitCode)) {
        throw errorResult;
      }
      return elseMapper.apply(charset, outputResult, errorResult, exitCode);
    };
  }

  @FunctionalInterface
  public interface StreamFunction<R> {

    R apply(Charset charset, InputStream output) throws IOException;

    default <RS> StreamFunction<RS> map(Function<R, RS> resultMapper) {
      return (charset, output) -> resultMapper.apply(apply(charset, output));
    }
  }

  @FunctionalInterface
  public interface StreamSupplier {

    void get(Charset charset, OutputStream input) throws IOException, InterruptedException;

    default StreamSupplier andThen(StreamSupplier inputSupplier) {
      return (charset, input) -> {
        get(charset, input);
        inputSupplier.get(charset, input);
      };
    }
  }

  @FunctionalInterface
  public interface ResultFunction<O, E, R> {

    R apply(Charset charset, O outputResult, E errorResult, int exitCode) throws Exception;
  }
}
