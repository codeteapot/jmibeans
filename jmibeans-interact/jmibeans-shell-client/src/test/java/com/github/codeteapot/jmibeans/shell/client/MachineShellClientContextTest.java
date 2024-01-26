package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKeyType.RSA;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostStatus.KNOWN;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("integration")
@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension.class)
class MachineShellClientContextTest {

  private static final String KNOWN_USERNAME = "scott";
  private static final char[] KNOWN_PASSWORD = new char[] {'1', '2', '3', '4'};

  private static final MachineShellIdentityName KNOWN_IDENTITY_NAME =
      new MachineShellIdentityName("known-identity");

  private OpenSSHServer server;
  
  @Mock
  private Predicate<InetAddress> allowHost;
  
  private JSchMachineShellClientEngine engine;
  private JSchKeyPairIdentity knownIdentity;

  @BeforeAll
  void init(@TempDir Path serverTempDir, @Mock Predicate<InetAddress> allowHost) throws Exception {
    this.allowHost = requireNonNull(allowHost);
    engine = new JSchMachineShellClientEngine(callbacks -> Stream.of(callbacks)
        .filter(MachineShellHostCallback.class::isInstance)
        .map(MachineShellHostCallback.class::cast)
        .filter(callback -> allowHost.test(callback.getAddress()))
        .findAny()
        .ifPresent(callback -> callback.setStatus(KNOWN)));
    knownIdentity = new JSchKeyPairIdentity(engine, KNOWN_IDENTITY_NAME, RSA, 1024);
    server = new OpenSSHServer(
        serverTempDir,
        KNOWN_USERNAME,
        KNOWN_PASSWORD,
        knownIdentity::writePublicKey);
    server.start();
  }

  @AfterAll
  void destroy() {
    server.stop();
  }

  @Nested
  class MachineShellClientContextCommandTest extends MachineShellClientContextCommnadNestedTest {

    MachineShellClientContextCommandTest() {
      super(server, allowHost, engine, KNOWN_USERNAME, knownIdentity);
    }
  }
}
