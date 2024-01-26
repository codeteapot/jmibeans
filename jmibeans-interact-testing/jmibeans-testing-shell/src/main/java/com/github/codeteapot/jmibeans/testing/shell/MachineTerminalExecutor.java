package com.github.codeteapot.jmibeans.testing.shell;

import static java.util.concurrent.Executors.newCachedThreadPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;

class MachineTerminalExecutor {

  private final ExecutorService executor;

  MachineTerminalExecutor() {
    this.executor = newCachedThreadPool();
  }

  <R> MachineTerminalExecutionResult<R> execute(
      MachineTerminalBehavior behavior,
      MachineShellCommandExecution<R> execution,
      long timeout,
      TimeUnit unit) throws IOException {
    try (
        PipedInputStream output = new PipedInputStream();
        PipedInputStream error = new PipedInputStream();
        PipedOutputStream input = new PipedOutputStream()) {
      Semaphore semaphore = new Semaphore(0);
      int exitCode = execute(behavior, execution, output, error, input, semaphore);
      semaphore.tryAcquire(3, timeout, unit);
      return new MachineTerminalExecutionResult<>(execution.mapResult(exitCode));
    } catch (InterruptedException e) {
      return new MachineTerminalExecutionResult<>(e);
    } catch (Exception e) {
      return new MachineTerminalExecutionResult<>(new MachineShellCommandExecutionException(e));
    }
  }

  private int execute(
      MachineTerminalBehavior behavior,
      MachineShellCommandExecution<?> execution,
      PipedInputStream output,
      PipedInputStream error,
      PipedOutputStream input,
      Semaphore semaphore) throws IOException {
    try (MachineTerminalExecutorContext context = new MachineTerminalExecutorContext(
        output,
        error,
        input)) {
      executor.submit(() -> handleOutput(execution, output, semaphore));
      executor.submit(() -> handleError(execution, error, semaphore));
      executor.submit(() -> handleInput(execution, input, semaphore));
      return behavior.execute(context);
    }
  }

  private Void handleOutput(
      MachineShellCommandExecution<?> execution,
      InputStream output,
      Semaphore semaphore) throws Exception {
    try {
      execution.handleOutput(output);
      return null;
    } finally {
      semaphore.release();
    }
  }

  private Void handleError(
      MachineShellCommandExecution<?> execution,
      InputStream error,
      Semaphore semaphore) throws Exception {
    try {
      execution.handleError(error);
      return null;
    } finally {
      semaphore.release();
    }
  }

  private Void handleInput(
      MachineShellCommandExecution<?> execution,
      OutputStream input,
      Semaphore semaphore) throws Exception {
    try {
      execution.handleInput(input);
      return null;
    } finally {
      semaphore.release();
    }
  }
}
