package com.github.codeteapot.jmibeans.shell.client;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Level.FINE;
import static java.util.logging.Logger.getLogger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

class JschMachineShellClientConnectionAvailableState extends MachineShellClientConnectionState {

  private static final int CONNECTION_TIMEOUT = 2000;

  private static final Logger logger = getLogger(JschMachineShellClientConnection.class.getName());

  private final Session jschSession;
  private final long executionTimeoutMillis;
  private final ExecutorService handlerExecutor;
  private final Set<JschMachineShellClientFile> files;

  JschMachineShellClientConnectionAvailableState(
      MachineShellClientConnectionStateChanger stateChanger,
      MachineShellClientConnectionEventSource eventSource,
      Session jschSession,
      long executionTimeoutMillis) {
    super(stateChanger, eventSource);
    this.jschSession = requireNonNull(jschSession);
    this.executionTimeoutMillis = executionTimeoutMillis;
    handlerExecutor = newCachedThreadPool();
    files = new HashSet<>();
  }

  @Override
  <R> R execute(MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    try {
      return execute((ChannelExec) jschSession.openChannel("exec"), command);
    } catch (JSchException e) {
      checkErrorOccurred(e);
      throw new MachineShellClientException(e);
    }
  }

  @Override
  MachineShellClientFile file(String path) throws MachineShellClientException {
    try {
      JschMachineShellClientFile file = new JschMachineShellClientFile(
          (ChannelSftp) jschSession.openChannel("sftp"),
          path);
      files.add(file);
      return file;
    } catch (JSchException e) {
      checkErrorOccurred(e);
      throw new MachineShellClientException(e);
    }
  }

  @Override
  void close() throws Exception {
    stateChanger.unavailable();
    jschSession.disconnect(); // Not tested because state changed to unavailable
    handlerExecutor.shutdownNow();
    files.removeIf(JschMachineShellClientFile::detach);
    eventSource.fireClosedEvent();
  }

  private void checkErrorOccurred(Exception cause) {
    if (!jschSession.isConnected()) {
      stateChanger.unavailable();
      eventSource.fireErrorOccurredEvent(new MachineShellClientException(cause));
    }
  }

  private <R> R execute(ChannelExec jschChannel, MachineShellClientCommand<R> command)
      throws MachineShellClientException, MachineShellClientCommandExecutionException {
    try {
      long initialTime = currentTimeMillis();
      String statement = command.getStatement();
      MachineShellClientCommandExecution<R> execution = command.getExecution(defaultCharset());

      logger.log(FINE, "Executing SSH command: {0}", statement);

      InputStream output = jschChannel.getInputStream();
      InputStream error = jschChannel.getErrStream();
      OutputStream input = jschChannel.getOutputStream();
      jschChannel.setCommand(statement);
      jschChannel.connect(CONNECTION_TIMEOUT);

      Future<Void> outputTask = handlerExecutor.submit(() -> {
        execution.handleOutput(output);
        return null;
      });
      Future<Void> errorTask = handlerExecutor.submit(() -> {
        execution.handleError(error);
        return null;
      });
      Future<Void> inputTask = handlerExecutor.submit(() -> {
        execution.handleInput(input);
        return null;
      });
      while (currentTimeMillis() - initialTime < executionTimeoutMillis) {
        if (jschChannel.isClosed() && outputTask.isDone() && errorTask.isDone()) {
          inputTask.cancel(true);
          logger.log(FINE, "SSH command completed: {0}", statement);
          return mapResult(execution, jschChannel.getExitStatus());
        }
      }
      inputTask.cancel(true);
      throw new TimeoutException(format("Timeout: %dms", currentTimeMillis() - initialTime));
    } catch (JSchException e) {
      checkErrorOccurred(e);
      throw new MachineShellClientException(e);
    } catch (TimeoutException e) {
      checkErrorOccurred(e);
      throw new MachineShellClientCommandExecutionException(e.getMessage());
    } catch (ExecutionException e) {
      checkErrorOccurred(e);
      throw new MachineShellClientCommandExecutionException(e.getCause());
    } catch (IOException e) {
      checkErrorOccurred(e);
      throw new MachineShellClientCommandExecutionException(e);
    } finally {
      jschChannel.disconnect();
    }
  }

  private static <R> R mapResult(MachineShellClientCommandExecution<R> execution, int exitCode)
      throws ExecutionException {
    try {
      return execution.mapResult(exitCode);
    } catch (Exception e) {
      throw new ExecutionException(e);
    }
  }
}
