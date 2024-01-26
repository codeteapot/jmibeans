package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.InetAddressCreator.getByName;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.MachineSessionPool;
import com.github.codeteapot.jmibeans.PooledMachineSessionFactory;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.session.MachineSession;
import com.github.codeteapot.jmibeans.session.MachineSessionAuthentication;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PooledMachineSessionFactoryAcceptanceTest {

  private static final InetAddress ANY_HOST = getByName("0.0.0.0");
  private static final int ANY_PORT = 0;

  private static final Duration LITTLE_IDLE_TIMEOUT = ofMillis(240L);

  private static final MachineRef SOME_REF = new MachineRef(
      new byte[] {0x0a},
      new byte[] {0x01});

  private static final String SOME_USERNAME = "some-username";


  @Mock
  private MachineSessionFactory managedSessionFactory;

  @Mock
  private MachineSessionPool sessionPool;

  private MachineSessionFactory sessionFactory;

  @BeforeEach
  public void setUp() {
    sessionFactory = new PooledMachineSessionFactory(
        managedSessionFactory,
        newScheduledThreadPool(1),
        SOME_REF,
        sessionPool);
  }

  @Test
  public void reuseSession(
      @Mock MachineSessionAuthentication anyAuthentication,
      @Mock MachineSession anyManagedSession) throws Exception {
    when(managedSessionFactory.getSession(any(), anyInt(), eq(SOME_USERNAME), any()))
        .thenReturn(anyManagedSession);
    when(sessionPool.getIdleTimeout())
        .thenReturn(Optional.empty());

    MachineSession firstSession = sessionFactory.getSession(
        ANY_HOST,
        ANY_PORT,
        SOME_USERNAME,
        anyAuthentication);
    firstSession.close();
    MachineSession secondSession = sessionFactory.getSession(
        ANY_HOST,
        ANY_PORT,
        SOME_USERNAME,
        anyAuthentication);

    assertThat(firstSession).isEqualTo(secondSession);
  }

  @Test
  public void doNotReuseSessionAfterPhysicalClose(
      @Mock MachineSessionAuthentication someAuthentication,
      @Mock MachineSession someManagedSession) throws Exception {
    when(managedSessionFactory.getSession(any(), anyInt(), eq(SOME_USERNAME), any()))
        .thenReturn(someManagedSession);
    when(sessionPool.getIdleTimeout())
        .thenReturn(Optional.of(LITTLE_IDLE_TIMEOUT));

    MachineSession firstSession = sessionFactory.getSession(
        ANY_HOST,
        ANY_PORT,
        SOME_USERNAME,
        someAuthentication);
    firstSession.close();
    await().untilAsserted(() -> {
      verify(someManagedSession).close();
    });
    MachineSession secondSession = sessionFactory.getSession(
        ANY_HOST,
        ANY_PORT,
        SOME_USERNAME,
        someAuthentication);

    assertThat(firstSession).isNotEqualTo(secondSession);
  }
}
