package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKeyType.RSA;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostStatus.KNOWN;
import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import java.nio.file.Path;
import java.util.stream.Stream;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;

@Disabled // TODO Enable after unit test coverage completed
@Tag("integration")
@ExtendWith(MockitoExtension.class)
class MachineShellClientContextAcceptanceTest {

  private static final char[] ANY_PASSWORD = new char[0];

  private static final MachineShellIdentityName ANY_IDENTITY_NAME =
      new MachineShellIdentityName("some-identity");

  private static final MachineShellPublicKeyType ANY_PUBLIC_KEY_TYPE = RSA;
  private static final int ANY_PUBLIC_KEY_SIZE = 1024;

  private static final String KNOWN_USERNAME = "scott";

  private static final int SOME_DIVIDEND = 9;
  private static final int SOME_DIVISOR = 4;
  private static final int SOME_DIVISION = 2;

  @Test
  void executeCommandSuccessfully(
      @Mock CallbackHandler callbackHandler,
      @TempDir Path serverTempDir) throws Exception {
    MachineShellClientContext context = new MachineShellClientContext(callbackHandler);
    OpenSSHServer server = new OpenSSHServer(
        serverTempDir,
        KNOWN_USERNAME,
        ANY_PASSWORD,
        output -> context.generateIdentity(ANY_IDENTITY_NAME, new MachineShellPublicKey(
            ANY_PUBLIC_KEY_TYPE,
            ANY_PUBLIC_KEY_SIZE,
            output)));
    doAnswer(invocation -> {
      Stream.of(invocation.getArgument(0, Callback[].class))
          .filter(MachineShellHostCallback.class::isInstance)
          .map(MachineShellHostCallback.class::cast)
          .filter(callback -> getByName(server.getHost()).equals(callback.getAddress()))
          .findAny()
          .ifPresent(callback -> callback.setStatus(KNOWN));
      return null;
    }).when(callbackHandler).handle(any());
    server.start();

    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            server.getHost(),
            server.getPort(),
            KNOWN_USERNAME))) {

      int result = connection.execute(new TestDivideCommand(SOME_DIVIDEND, SOME_DIVISOR));

      assertThat(result).isEqualTo(SOME_DIVISION);
    }

    server.stop();
  }
}
