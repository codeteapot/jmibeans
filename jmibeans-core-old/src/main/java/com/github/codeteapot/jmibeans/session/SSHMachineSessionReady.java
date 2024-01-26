package com.github.codeteapot.jmibeans.session;

import static java.lang.System.currentTimeMillis;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Logger.getLogger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

class SSHMachineSessionReady implements SSHMachineSessionState {

  private static final int CONNECTION_TIMEOUT = 2000;
  private static final int BUFFER_SIZE = 4 * 1024;

  private static final Logger logger = getLogger(SSHMachineSessionReady.class.getName());

  private final SSHMachineSessionStateChanger stateChanger;
  private final SSHMachineSessionPasswordMapper passwordMapper;
  private final long executionTimeoutMillis;
  private final JSch jsch;
  private final InetAddress host;
  private final Integer port;
  private final String username;
  private final MachineSessionAuthentication authentication;
  private final ExecutorService handlerExecutor;
  private final Set<SSHMachineSessionFile> files;
  private Session jschSession;

  SSHMachineSessionReady(
      SSHMachineSessionStateChanger stateChanger,
      SSHMachineSessionPasswordMapper passwordMapper,
      long executionTimeoutMillis,
      JSch jsch,
      InetAddress host,
      Integer port,
      String username,
      MachineSessionAuthentication authentication) {
    this.stateChanger = requireNonNull(stateChanger);
    this.passwordMapper = requireNonNull(passwordMapper);
    this.executionTimeoutMillis = executionTimeoutMillis;
    this.jsch = requireNonNull(jsch);
    this.host = requireNonNull(host);
    this.port = port;
    this.username = requireNonNull(username);
    this.authentication = requireNonNull(authentication);
    handlerExecutor = newCachedThreadPool();
    files = new HashSet<>();
    jschSession = null;
  }

  @Override
  public <R> R execute(MachineCommand<R> command)
      throws MachineSessionException, MachineCommandExecutionException {
    try {
      return execute((ChannelExec) getSession().openChannel("exec"), command);
    } catch (JSchException e) {
      throw new MachineSessionException(e);
    }
  }

  @Override
  public MachineSessionFile file(String path) throws MachineSessionException {
    try {
      SSHMachineSessionFile file = new SSHMachineSessionFile(
          (ChannelSftp) getSession().openChannel("sftp"),
          path);
      files.add(file);
      return file;
    } catch (JSchException e) {
      throw new MachineSessionException(e);
    }
  }

  public void close() throws IOException {
    files.removeIf(SSHMachineSessionFile::detach);
    handlerExecutor.shutdownNow();
    ofNullable(jschSession).ifPresent(Session::disconnect);
    stateChanger.stateChange(new SSHMachineSessionClosed());
  }

  private Session getSession() throws JSchException {
    if (jschSession == null) { // Precondition: !jschSession.isConnected()
      jschSession = ofNullable(port)
          .map(this::withSpecifiedPort)
          .orElseGet(this::withDefaultPort)
          .perform(username, host);
      authentication.authenticate(new SSHMachineSessionAuthenticationContext(
          jschSession,
          passwordMapper));
      jschSession.connect(CONNECTION_TIMEOUT);
    }
    return jschSession;
  }

  private GetSessionAction withSpecifiedPort(int port) {
    return (username, host) -> jsch.getSession(username, host.getHostAddress(), port);
  }

  private GetSessionAction withDefaultPort() {
    return (username, host) -> jsch.getSession(username, host.getHostAddress());
  }

  private <R> R execute(ChannelExec jschChannel, MachineCommand<R> command)
      throws MachineSessionException, MachineCommandExecutionException {
    try {
      PipedOutputStream targetOutput = new PipedOutputStream();
      PipedOutputStream targetError = new PipedOutputStream();
      try (
          PipedInputStream handledOutput = new PipedInputStream(targetOutput);
          PipedInputStream handledError = new PipedInputStream(targetError)) {
        long initialTime = currentTimeMillis();
        String sentence = command.getSentence();
        MachineCommandExecution<R> execution = command.getExecution(defaultCharset());

        logger.fine(new StringBuilder()
            .append("Executing SSH command: ").append(sentence)
            .toString());

        InputStream output = jschChannel.getInputStream();
        InputStream error = jschChannel.getErrStream();
        jschChannel.setCommand(sentence);
        jschChannel.connect(CONNECTION_TIMEOUT);

        Future<Void> outputTask = handlerExecutor.submit(() -> {
          execution.handleOutput(handledOutput);
          return null;
        });
        Future<Void> errorTask = handlerExecutor.submit(() -> {
          execution.handleError(handledError);
          return null;
        });
        handlerExecutor.submit(() -> {
          execution.handleInput(jschChannel.getOutputStream());
          return null;
        });
        byte[] buffer = new byte[BUFFER_SIZE];
        boolean targetClosed = false;
        while (currentTimeMillis() - initialTime < executionTimeoutMillis) {
          if (targetClosed) {
            if (outputTask.isDone() && errorTask.isDone()) {
              logger.fine(new StringBuilder()
                  .append("SSH command completed: ").append(sentence)
                  .toString());
              return execution.mapResult(jschChannel.getExitStatus());
            }
          } else if (jschChannel.isClosed()) {
            targetOutput.close();
            targetError.close();
            targetClosed = true;
          } else {
            if (output.available() > 0) {
              int len = output.read(buffer, 0, BUFFER_SIZE);
              targetOutput.write(buffer, 0, len);
            }
            if (error.available() > 0) {
              int len = error.read(buffer, 0, BUFFER_SIZE);
              targetError.write(buffer, 0, len);
            }
          }
        }
      }
      targetOutput.close();
      targetError.close();
      throw new MachineCommandExecutionException("Execution timeout");
    } catch (JSchException e) {
      throw new MachineSessionException(e);
    } catch (Exception e) {
      throw new MachineCommandExecutionException(e);
    } finally {
      jschChannel.disconnect();
    }
  }
}
