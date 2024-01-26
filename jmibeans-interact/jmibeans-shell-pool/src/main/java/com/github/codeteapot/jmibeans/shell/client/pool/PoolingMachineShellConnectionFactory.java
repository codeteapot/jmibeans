package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnectionFactory;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;

// TODO DESIGN: Implement maxConnextions property
public class PoolingMachineShellConnectionFactory implements MachineShellConnectionFactory {

  public static final Duration DEFAULT_IDLE_TIMEOUT = ofSeconds(10L);

  private final MachineShellClientConnectionFactory clientConnectionFactory;
  private final MachineShellClientConnectionBridgeFactory clientConnectionBridgeFactory;
  private final Collection<PooledMachineShellClientConnection> connections;
  private final PooledMachineShellClientConnectionConstructor pooledConnectionConstructor;

  private Duration idleTimeout;

  public PoolingMachineShellConnectionFactory(
      MachineShellClientConnectionFactory clientConnectionFactory) {
    this(
        clientConnectionFactory,
        new LinkedList<>(),
        newSingleThreadScheduledExecutor(),
        MachineShellClientConnectionBridgeFactory::new,
        PooledMachineShellClientConnection::new);
  }

  PoolingMachineShellConnectionFactory(
      MachineShellClientConnectionFactory clientConnectionFactory,
      Collection<PooledMachineShellClientConnection> connections,
      ScheduledExecutorService closeExecutor,
      MachineShellClientConnectionBridgeFactoryConstructor bridgeFactoryConstructor,
      PooledMachineShellClientConnectionConstructor pooledConnectionConstructor) {
    this.clientConnectionFactory = requireNonNull(clientConnectionFactory);
    this.connections = requireNonNull(connections);
    this.pooledConnectionConstructor = requireNonNull(pooledConnectionConstructor);
    clientConnectionBridgeFactory = bridgeFactoryConstructor.construct(
        closeExecutor,
        () -> idleTimeout);
    idleTimeout = DEFAULT_IDLE_TIMEOUT;
  }

  @Override
  public MachineShellConnection getConnection(String username) throws MachineShellException {
    try {
      return connections.stream()
          .filter(connection -> connection.request(username))
          .findAny()
          .map(this::availableConnection)
          .orElseGet(this::newConnection)
          .acquire(username)
          .getConnection();
    } catch (MachineShellClientException e) {
      throw new MachineShellException(e);
    }
  }

  // Covered on PoolingMachineShellConnectionFactoryAcceptanceTest
  public void setIdleTimeout(Duration idleTimeout) {
    this.idleTimeout = requireNonNull(idleTimeout);
  }

  public void cleanup() {
    connections.forEach(PooledMachineShellClientConnection::closeNow);
  }

  private PooledMachineShellClientConnectionAcquire availableConnection(
      PooledMachineShellClientConnection connection) {
    return username -> {
      connection.acquire();
      return connection;
    };
  }

  private PooledMachineShellClientConnectionAcquire newConnection() {
    return username -> {
      MachineShellClientConnection managed = clientConnectionFactory.getConnection(username);
      PooledMachineShellClientConnection connection = pooledConnectionConstructor.construct(
          clientConnectionBridgeFactory.getBridge(managed),
          username,
          connections::remove);
      managed.addConnectionEventListener(connection);
      connections.add(connection);
      return connection;
    };
  }
}
