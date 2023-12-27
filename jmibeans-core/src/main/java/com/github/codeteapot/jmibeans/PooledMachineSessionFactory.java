package com.github.codeteapot.jmibeans;

import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.session.MachineSession;
import com.github.codeteapot.jmibeans.session.MachineSessionAuthentication;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

class PooledMachineSessionFactory implements MachineSessionFactory, MachineSessionPoolReleaser {

  private static final Duration DEFAULT_IDLE_TIMEOUT = ofSeconds(20L);

  private static final Logger logger = getLogger(PooledMachineSessionFactory.class.getName());

  private final MachineSessionFactory managedSessionFactory;
  private final ScheduledExecutorService releaseExecutor;
  private final MachineRef machineRef;
  private final MachineSessionPool sessionPool;
  private final Collection<PooledMachineSession> sessions;
  private final PooledMachineSessionConstructor pooledSessionConstructor;

  PooledMachineSessionFactory(
      MachineSessionFactory managedSessionFactory,
      ScheduledExecutorService releaseExecutor,
      MachineRef machineRef,
      MachineSessionPool sessionPool) {
    this(
        managedSessionFactory,
        releaseExecutor,
        machineRef,
        sessionPool,
        new LinkedList<>(),
        PooledMachineSession::new);
  }

  PooledMachineSessionFactory(
      MachineSessionFactory managedSessionFactory,
      ScheduledExecutorService releaseExecutor,
      MachineRef machineRef,
      MachineSessionPool sessionPool,
      Collection<PooledMachineSession> sessions,
      PooledMachineSessionConstructor pooledSessionConstructor) {
    this.managedSessionFactory = requireNonNull(managedSessionFactory);
    this.releaseExecutor = requireNonNull(releaseExecutor);
    this.machineRef = requireNonNull(machineRef);
    this.sessionPool = requireNonNull(sessionPool);
    this.sessions = requireNonNull(sessions);
    this.pooledSessionConstructor = requireNonNull(pooledSessionConstructor);
  }

  @Override
  public MachineSession getSession(
      InetAddress host,
      Integer port,
      String username,
      MachineSessionAuthentication authentication) {
    return sessions.stream()
        .filter(session -> session.available(username))
        .findAny()
        .map(this::availableSession)
        .orElseGet(() -> newSession(host, port, authentication))
        .acquire(username);
  }

  @Override
  public void releaseAll() {
    sessions.forEach(PooledMachineSession::releaseNow);
  }

  private AcquireMachineSessionClause availableSession(PooledMachineSession session) {
    return username -> {
      session.acquire();
      logger.fine(new StringBuilder()
          .append("Reusing machine session for user ").append(username)
          .append(" on machine ").append(machineRef)
          .append(": ").append(session)
          .toString());
      return session;
    };
  }

  private AcquireMachineSessionClause newSession(
      InetAddress host,
      Integer port,
      MachineSessionAuthentication authentication) {
    return username -> {
      PooledMachineSession session = pooledSessionConstructor.construct(
          managedSessionFactory.getSession(
              host,
              port,
              username,
              authentication),
          releaseExecutor,
          sessions::remove,
          username,
          sessionPool.getIdleTimeout().orElse(DEFAULT_IDLE_TIMEOUT));
      logger.fine(new StringBuilder()
          .append("Adding new session for user ").append(username)
          .append(" on machine ").append(machineRef).append(": ").append(session)
          .toString());
      sessions.add(session);
      return session;
    };
  }
}
