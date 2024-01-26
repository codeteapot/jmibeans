package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

abstract class MachineShellClientContextCommnadNestedTest {

  private static final int ANY_DIVIDEND = 1;
  private static final int ANY_DIVISOR = 1;

  private static final long ENOUGH_EXECUTION_TIMEOUT_MILLIS = 2200L;

  private static final int SOME_DIVIDEND = 9;
  private static final int SOME_DIVISOR = 4;
  private static final int SOME_DIVISION = 2;

  private final OpenSSHServer server;
  private final Predicate<InetAddress> allowHost;
  private final JSchMachineShellClientEngine engine;
  private final String knownUsername;
  private final JSchKeyPairIdentity knownIdentity;

  private Set<JSchKeyPairIdentity> identities;

  @Mock
  private Supplier<Long> executionTimeoutMillisSupplier;

  @Mock
  private JSchKeyPairIdentityConstructor identityConstructor;

  private MachineShellClientContext context;

  MachineShellClientContextCommnadNestedTest(
      OpenSSHServer server,
      Predicate<InetAddress> allowHost,
      JSchMachineShellClientEngine engine,
      String knownUsername,
      JSchKeyPairIdentity knownIdentity) {
    this.server = requireNonNull(server);
    this.allowHost = requireNonNull(allowHost);
    this.engine = requireNonNull(engine);
    this.knownUsername = requireNonNull(knownUsername);
    this.knownIdentity = requireNonNull(knownIdentity);
  }

  @BeforeEach
  void setUp() throws Exception {
    identities = new HashSet<>();
    context = new MachineShellClientContext(
        engine,
        identities,
        executionTimeoutMillisSupplier,
        identityConstructor);
  }

  @Test
  void executeCommandWithIdentity(
      @Mock MachineShellClientConnectionListener someConnectionListener) throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    when(allowHost.test(getByName(server.getHost()))).thenReturn(true);
    identities.add(knownIdentity);
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            server.getHost(),
            server.getPort(),
            knownUsername))) {
      connection.addConnectionEventListener(someConnectionListener);

      int result = connection.execute(new TestDivideCommand(SOME_DIVIDEND, SOME_DIVISOR));

      assertThat(result).isEqualTo(SOME_DIVISION);
    }
    verify(someConnectionListener).connectionClosed(argThat(event -> !event.getClientException()
        .isPresent()));
  }

  @Test
  void failWhenExecutingCommandWithConnectionError(
      @Mock MachineShellClientConnectionListener someConnectionListener) throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    when(allowHost.test(getByName(server.getHost()))).thenReturn(true);
    identities.add(knownIdentity);
    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            server.getHost(),
            server.getPort(),
            knownUsername));
    connection.addConnectionEventListener(someConnectionListener);
    server.disconnect();

    Throwable e = catchThrowable(() -> connection.execute(new TestDivideCommand(
        ANY_DIVIDEND,
        ANY_DIVISOR)));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCauseInstanceOf(Exception.class);
    verify(someConnectionListener)
        .connectionErrorOccurred(argThat(event -> event.getClientException()
            .map(clientException -> clientException.getCause() instanceof Exception)
            .orElse(false)));
    server.reconnect();
  }
}
