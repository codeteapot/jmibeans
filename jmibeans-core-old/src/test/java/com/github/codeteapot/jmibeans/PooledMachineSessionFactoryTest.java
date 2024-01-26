package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.InetAddressCreator.getByName;
import static com.github.codeteapot.testing.InetAddressCreator.getLocalHost;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.MachineSessionPool;
import com.github.codeteapot.jmibeans.PooledMachineSession;
import com.github.codeteapot.jmibeans.PooledMachineSessionConstructor;
import com.github.codeteapot.jmibeans.PooledMachineSessionFactory;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.session.MachineSession;
import com.github.codeteapot.jmibeans.session.MachineSessionAuthentication;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PooledMachineSessionFactoryTest {

  private static final InetAddress ANY_HOST = getByName("0.0.0.0");
  private static final Integer ANY_PORT = null;

  private static final boolean AVAILABLE = true;

  private static final MachineRef REF = new MachineRef(new byte[] {0x0a}, new byte[] {0x01});

  private static final InetAddress SOME_HOST = getLocalHost();
  private static final int SOME_PORT = 1000;
  private static final String SOME_USERNAME = "some-username";

  private static final Duration SOME_IDLE_TIMEOUT = ofMillis(420);

  @Mock
  private MachineSessionFactory managedSessionFactory;

  @Mock
  private ScheduledExecutorService releaseExecutor;

  @Mock
  private MachineSessionPool sessionPool;

  private Collection<PooledMachineSession> sessions;

  @Mock
  private PooledMachineSessionConstructor pooledSessionConstructor;

  private PooledMachineSessionFactory sessionFactory;

  @BeforeEach
  public void setUp() {
    sessions = new LinkedList<>();
    sessionFactory = new PooledMachineSessionFactory(
        managedSessionFactory,
        releaseExecutor,
        REF,
        sessionPool,
        sessions,
        pooledSessionConstructor);
  }

  @Test
  public void giveNewlyCreatedSession(
      @Mock MachineSessionAuthentication someAuthentication,
      @Mock MachineSession someManagedSession,
      @Mock PooledMachineSession someSession) {
    when(sessionPool.getIdleTimeout())
        .thenReturn(Optional.of(SOME_IDLE_TIMEOUT));
    when(pooledSessionConstructor.construct(
        eq(someManagedSession),
        eq(releaseExecutor),
        any(),
        eq(SOME_USERNAME),
        eq(SOME_IDLE_TIMEOUT))).thenReturn(someSession);
    when(managedSessionFactory.getSession(
        SOME_HOST,
        SOME_PORT,
        SOME_USERNAME,
        someAuthentication)).thenReturn(someManagedSession);

    MachineSession session = sessionFactory.getSession(
        SOME_HOST,
        SOME_PORT,
        SOME_USERNAME,
        someAuthentication);

    assertThat(session).isEqualTo(someSession);
    assertThat(sessions).containsExactly(someSession);
    // Acquire is not expected since pooled session is constructed as released
  }

  @Test
  public void giveAvailableSession(
      @Mock MachineSessionAuthentication anyAuthentication,
      @Mock PooledMachineSession someSession) {
    sessions.add(someSession);
    when(someSession.available(SOME_USERNAME))
        .thenReturn(AVAILABLE);

    MachineSession session = sessionFactory.getSession(
        ANY_HOST,
        ANY_PORT,
        SOME_USERNAME,
        anyAuthentication);

    assertThat(session).isEqualTo(someSession);
    assertThat(sessions).containsExactly(someSession);
    verify(someSession).acquire();
  }

  @Test
  public void releaseAvailableSessions(@Mock PooledMachineSession someSession) {
    sessions.add(someSession);

    sessionFactory.releaseAll();

    verify(someSession).releaseNow();
  }
}
