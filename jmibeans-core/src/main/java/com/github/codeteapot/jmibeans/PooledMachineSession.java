package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.session.MachineCommand;
import com.github.codeteapot.jmibeans.session.MachineCommandExecutionException;
import com.github.codeteapot.jmibeans.session.MachineSession;
import com.github.codeteapot.jmibeans.session.MachineSessionException;
import com.github.codeteapot.jmibeans.session.MachineSessionFile;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Logger;

class PooledMachineSession implements MachineSession {

  private static final Logger logger = getLogger(PooledMachineSession.class.getName());

  private final MachineSession managedSession;
  private final ScheduledExecutorService releaseExecutor;
  private final Consumer<PooledMachineSession> removeAction;
  private final String username;
  private final Duration idleTimeout;
  private final AtomicReference<ScheduledFuture<Void>> releaseFuture;

  PooledMachineSession(
      MachineSession managedSession,
      ScheduledExecutorService releaseExecutor,
      Consumer<PooledMachineSession> removeAction,
      String username,
      Duration idleTimeout) {
    this(
        managedSession,
        releaseExecutor,
        removeAction,
        username,
        idleTimeout,
        new AtomicReference<>());
  }

  PooledMachineSession(
      MachineSession managedSession,
      ScheduledExecutorService releaseExecutor,
      Consumer<PooledMachineSession> removeAction,
      String username,
      Duration idleTimeout,
      AtomicReference<ScheduledFuture<Void>> releaseFuture) {
    this.managedSession = requireNonNull(managedSession);
    this.releaseExecutor = requireNonNull(releaseExecutor);
    this.removeAction = requireNonNull(removeAction);
    this.username = requireNonNull(username);
    this.idleTimeout = requireNonNull(idleTimeout);
    this.releaseFuture = requireNonNull(releaseFuture);
  }

  @Override
  public <R> R execute(MachineCommand<R> command)
      throws MachineSessionException, MachineCommandExecutionException {
    // TODO Check closed
    return managedSession.execute(command);
  }

  @Override
  public MachineSessionFile file(String path) throws MachineSessionException {
    // TODO Check closed
    return managedSession.file(path);
  }

  @Override
  public void close() throws IOException {
    // TODO Check closed
    releaseFuture.set(releaseExecutor.schedule(
        this::removeAndClose, // TODO Needs an acceptance test
        idleTimeout.toMillis(),
        MILLISECONDS));
  }

  boolean available(String username) {
    return releaseFuture.get() != null && username.equals(this.username);
  }

  void acquire() {
    releaseFuture.getAndSet(null).cancel(true);
  }

  void releaseNow() {
    try {
      releaseFuture.get().cancel(true);
      removeAndClose();
    } catch (IOException e) {
      logger.log(WARNING, "Unable to close managed session", e);
    }
  }

  private Void removeAndClose() throws IOException {
    removeAction.accept(this);
    managedSession.close();
    return null;
  }
}
