package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.Arrays;

import javax.security.auth.callback.UnsupportedCallbackException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.client.secutity.auth.MachineShellIdentityName;

@ExtendWith(MockitoExtension.class)
class MachineShellClientTest {

  private static final String ANY_USERNAME = "any";

  private static final InetAddress SOME_HOST = getByName("1.1.1.1");
  private static final int SOME_PORT = 2000;

  private static final String SOME_HOST_ADDRESS = "1.1.1.1";
  private static final String SOME_USERNAME = "scott";
  private static final String SOME_UPPERCASE_USERNAME = "SCOTT";
  private static final MachineShellIdentityName SOME_IDENTITY_ONLY = new MachineShellIdentityName(
      "some-identity");
  private static final char[] SOME_PASSWORD = {'1', '2', '3', '4'};

  @Mock
  private MachineShellClientContext context;

  private TestMachineShellClientCallbackHandler callbackHandler;

  private MachineShellClient client;

  @BeforeEach
  void setUp() {
    callbackHandler = new TestMachineShellClientCallbackHandler(SOME_IDENTITY_ONLY, SOME_PASSWORD);
    client = new MachineShellClient(context, SOME_HOST, SOME_PORT, callbackHandler);
  }

  @Test
  void getConnectionSuccessfully(@Mock MachineShellClientConnection someConnection)
      throws Exception {
    when(context.getConnection(argThat(spec -> SOME_HOST_ADDRESS.equals(spec.getHostAddress())
        && spec.getPort().filter(isEqual(SOME_PORT)).isPresent()
        && SOME_UPPERCASE_USERNAME.equals(spec.getUsername())
        && spec.getIdentityOnly().filter(isEqual(SOME_IDENTITY_ONLY)).isPresent()
        && spec.getPassword().map(value -> Arrays.equals(SOME_PASSWORD, value)).orElse(false))))
            .thenReturn(someConnection);

    MachineShellClientConnection connection = client.getConnection(SOME_USERNAME);

    assertThat(connection).isEqualTo(someConnection);
  }

  @Test
  void failWhenGettingConnectionWithUnsupportedCallback() throws Exception {
    callbackHandler.setSupported(false);

    Throwable e = catchThrowable(() -> client.getConnection(ANY_USERNAME));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCauseInstanceOf(UnsupportedCallbackException.class);
  }
}
