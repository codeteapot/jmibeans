package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.InetAddressCreator.getByName;
import static com.github.codeteapot.testing.InetAddressCreator.getLocalHost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.MachineContextImpl;
import com.github.codeteapot.jmibeans.MachineRealm;
import com.github.codeteapot.jmibeans.machine.MachineContext;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.machine.UnknownUserException;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineNetworkName;
import com.github.codeteapot.jmibeans.session.MachineSession;
import com.github.codeteapot.jmibeans.session.MachineSessionAuthentication;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import java.net.InetAddress;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MachineContextTest {

  private static final InetAddress ANY_SESSION_HOST = getByName("0.0.0.0");

  private static final MachineRef REF = new MachineRef(new byte[] {0x0a}, new byte[] {0x01});
  private static final MachineNetworkName NETWORK_NAME = new MachineNetworkName("value");
  private static final int SESSION_PORT = 1000;

  private static final InetAddress SOME_SESSION_HOST = getLocalHost();
  private static final int SOME_SESSION_PORT = 1000;
  private static final String SOME_USERNAME = "some-username";

  @Mock
  private MachineRealm realm;

  @Mock
  private MachineLink link;

  @Mock
  private MachineSessionFactory sessionFactory;

  private MachineContext context;

  @BeforeEach
  public void setUp() {
    context = new MachineContextImpl(
        REF,
        realm,
        link,
        NETWORK_NAME,
        SESSION_PORT,
        sessionFactory);
  }

  @Test
  public void holdsRef() {
    MachineRef ref = context.getRef();

    assertThat(ref).isEqualTo(REF);
  }

  @Test
  public void giveSessionWithAuthenticationSuccess(
      @Mock MachineSessionAuthentication someAuthentication,
      @Mock MachineSession someSession) throws Exception {
    when(sessionFactory.getSession(
        SOME_SESSION_HOST,
        SOME_SESSION_PORT,
        SOME_USERNAME,
        someAuthentication)).thenReturn(someSession);
    when(link.getSessionHost(NETWORK_NAME))
        .thenReturn(SOME_SESSION_HOST);
    when(realm.authentication(SOME_USERNAME))
        .thenReturn(Optional.of(someAuthentication));

    MachineSession session = context.getSession(SOME_USERNAME);

    assertThat(session).isEqualTo(someSession);
  }

  @Test
  public void failsWhenGivingSessionWithAuthenticationFailure() throws Exception {
    when(link.getSessionHost(NETWORK_NAME))
        .thenReturn(ANY_SESSION_HOST);
    when(realm.authentication(SOME_USERNAME))
        .thenReturn(Optional.empty());

    UnknownUserException e = (UnknownUserException) catchThrowable(() -> context.getSession(
        SOME_USERNAME));

    assertThat(e.getMachineRef()).isEqualTo(REF);
    assertThat(e.getUsername()).isEqualTo(SOME_USERNAME);
  }
}
