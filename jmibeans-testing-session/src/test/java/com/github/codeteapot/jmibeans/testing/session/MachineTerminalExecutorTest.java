package com.github.codeteapot.jmibeans.testing.session;

import static com.github.codeteapot.jmibeans.testing.session.TestMachineCommandExecution.execution;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.github.codeteapot.jmibeans.session.MachineCommandExecutionException;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalExecutionResult;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminalExecutor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MachineTerminalExecutorTest {

  private static final int ANY_EXIT_CODE = 0;
  private static final String ANY_RESULT_VALUE = "any-result-value";

  private static final long TEST_EXCUTION_TIMEOUT_MILLIS = 5;

  private static final int BUF_SIZE = 1024;

  private static final Void VOID_RESULT = null;

  private static final int SOME_EXIT_CODE = 14;

  private static final String SOME_RESULT_VALUE = "some-result-value";
  private static final String ANOTHER_RESULT_VALUE = "another-result-value";

  private static final Exception SOME_EXCEPTION = new Exception();

  private static final byte[] SOME_OUTPUT_BYTES = {1, 2, 3};
  private static final byte[] SOME_ERROR_BYTES = {4, 5, 6};
  private static final byte[] SOME_INPUT_BYTES = {7, 8, 9};

  private MachineTerminalExecutor executor;


  @BeforeEach
  public void setUp() {
    executor = new MachineTerminalExecutor();
  }

  @Test
  public void executeSuccessfully() throws Exception {
    MachineTerminalExecutionResult<String> result = executor.execute(
        context -> SOME_EXIT_CODE,
        execution(
            output -> {
            },
            error -> {
            },
            input -> {
            },
            exitCode -> {
              if (exitCode == SOME_EXIT_CODE) {
                return SOME_RESULT_VALUE;
              }
              return ANOTHER_RESULT_VALUE;
            }),
        TEST_EXCUTION_TIMEOUT_MILLIS,
        MILLISECONDS);

    assertThat(result.getValue().getValue()).hasValue(SOME_RESULT_VALUE);
  }

  @Test
  public void executeWithInterrupt() throws Exception {
    TestThreadFactory threadFactory = new TestThreadFactory();
    Semaphore waitSemaphore = new Semaphore(0);
    CompletableFuture<MachineTerminalExecutionResult<String>> futureResult = supplyAsync(
        () -> {
          try {
            return executor.execute(
                context -> ANY_EXIT_CODE,
                execution(
                    output -> {
                    },
                    error -> {
                    },
                    input -> {
                      waitSemaphore.release();
                      new Semaphore(0).acquire();
                    },
                    exitCode -> ANY_RESULT_VALUE),
                TEST_EXCUTION_TIMEOUT_MILLIS,
                MILLISECONDS);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        },
        newSingleThreadExecutor(threadFactory));
    waitSemaphore.acquire();
    threadFactory.interruptAll();
    Throwable e = catchThrowable(() -> futureResult.join().getValue());

    assertThat(e).isInstanceOf(InterruptedException.class);
  }

  @Test
  public void executeWithError() throws Exception {
    MachineTerminalExecutionResult<Void> result = executor.execute(
        context -> ANY_EXIT_CODE,
        execution(
            output -> {
            },
            error -> {
            },
            input -> {
            },
            exitCode -> {
              throw SOME_EXCEPTION;
            }),
        TEST_EXCUTION_TIMEOUT_MILLIS,
        MILLISECONDS);
    Throwable e = catchThrowable(() -> result.getValue().getValue());

    assertThat(e)
        .isInstanceOf(MachineCommandExecutionException.class)
        .hasCause(SOME_EXCEPTION);
  }

  @Test
  public void readFromOutput() throws Exception {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    MachineTerminalExecutionResult<byte[]> result = executor.execute(
        context -> {
          try {
            OutputStream output = context.getOutputStream();
            output.write(SOME_OUTPUT_BYTES);
            return ANY_EXIT_CODE;
          } catch (IOException e) {
            return ANY_EXIT_CODE;
          }
        },
        execution(
            output -> {
              byte[] buf = new byte[BUF_SIZE];
              int len = output.read(buf, 0, BUF_SIZE);
              while (len > 0) {
                bytes.write(buf, 0, len);
                len = output.read(buf, 0, BUF_SIZE);
              }
              bytes.flush();
            },
            error -> {
            },
            input -> {
            },
            exitCode -> bytes.toByteArray()),
        TEST_EXCUTION_TIMEOUT_MILLIS,
        MILLISECONDS);

    assertThat(result.getValue().getValue())
        .hasValueSatisfying(value -> assertThat(value).isEqualTo(SOME_OUTPUT_BYTES));
  }

  @Test
  public void readFromError() throws Exception {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    MachineTerminalExecutionResult<byte[]> result = executor.execute(
        context -> {
          try {
            OutputStream error = context.getErrorStream();
            error.write(SOME_ERROR_BYTES);
            return ANY_EXIT_CODE;
          } catch (IOException e) {
            return ANY_EXIT_CODE;
          }
        },
        execution(
            output -> {
            },
            error -> {
              byte[] buf = new byte[BUF_SIZE];
              int len = error.read(buf, 0, BUF_SIZE);
              while (len > 0) {
                bytes.write(buf, 0, len);
                len = error.read(buf, 0, BUF_SIZE);
              }
              bytes.flush();
            },
            input -> {
            },
            exitCode -> bytes.toByteArray()),
        TEST_EXCUTION_TIMEOUT_MILLIS,
        MILLISECONDS);

    assertThat(result.getValue().getValue())
        .hasValueSatisfying(value -> assertThat(value).isEqualTo(SOME_ERROR_BYTES));
  }

  @Test
  public void writeToInput() throws Exception {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    executor.execute(
        context -> {
          try {
            boolean received = false;
            InputStream input = context.getInputStream();
            byte[] buf = new byte[BUF_SIZE];
            while (!received || input.available() > 0) {
              int len = input.read(buf, 0, BUF_SIZE);
              bytes.write(buf, 0, len);
              received = true;
            }
            return ANY_EXIT_CODE;
          } catch (IOException e) {
            return ANY_EXIT_CODE;
          }
        },
        execution(
            output -> {
            },
            error -> {
            },
            input -> {
              input.write(SOME_INPUT_BYTES);
            },
            exitCode -> VOID_RESULT),
        TEST_EXCUTION_TIMEOUT_MILLIS,
        MILLISECONDS);

    assertThat(bytes.toByteArray()).isEqualTo(SOME_INPUT_BYTES);
  }
}
