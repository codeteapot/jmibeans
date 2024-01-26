package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellIdentityCallback;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

  @Mock
  private CallbackHandler callbackHandler;

  private MachineShellClient client;

  @BeforeEach
  void setUp() {
    client = new MachineShellClient(context, SOME_HOST, SOME_PORT, callbackHandler);
  }

  @Test
  void getConnectionSuccessfully(@Mock MachineShellClientConnection someConnection)
      throws Exception {
    doAnswer(invocation -> {
      Stream.of(invocation.getArgument(0, Callback[].class))
          .filter(NameCallback.class::isInstance)
          .map(NameCallback.class::cast)
          .filter(callback -> SOME_USERNAME.equals(callback.getName()))
          .findAny()
          .ifPresent(callback -> callback.setName(SOME_UPPERCASE_USERNAME));
      Stream.of(invocation.getArgument(0, Callback[].class))
          .filter(MachineShellIdentityCallback.class::isInstance)
          .map(MachineShellIdentityCallback.class::cast)
          .findAny()
          .ifPresent(callback -> callback.setIdentityOnly(SOME_IDENTITY_ONLY));
      Stream.of(invocation.getArgument(0, Callback[].class))
          .filter(PasswordCallback.class::isInstance)
          .map(PasswordCallback.class::cast)
          .findAny()
          .ifPresent(callback -> callback.setPassword(SOME_PASSWORD));
      return null;
    }).when(callbackHandler).handle(any());
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
  void failWhenGettingConnectionWithUnsupportedCallback(
      @Mock Callback anyUnsupportedCallback) throws Exception {
    doAnswer(invocation -> {
      throw new UnsupportedCallbackException(invocation.getArgument(0, Callback[].class)[0]);
    }).when(callbackHandler).handle(any());

    Throwable e = catchThrowable(() -> client.getConnection(ANY_USERNAME));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCauseInstanceOf(UnsupportedCallbackException.class);
  }
}
