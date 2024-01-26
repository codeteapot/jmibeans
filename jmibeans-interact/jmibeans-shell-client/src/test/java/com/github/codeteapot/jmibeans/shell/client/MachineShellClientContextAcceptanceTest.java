package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostStatus.KNOWN;
import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.stream.Stream;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Tag("integration")
@ExtendWith(MockitoExtension.class)
class MachineShellClientContextAcceptanceTest {

  private static final String CONTAINER_IMAGE_NAME = "lscr.io/linuxserver/openssh-server:latest";

  private static final String KNOWN_USERNAME = "scott";

  private static final MachineShellIdentityName SOME_IDENTITY_NAME =
      new MachineShellIdentityName("some-identity");

  private static final MachineShellPublicKeyType SOME_PUBLIC_KEY_TYPE =
      MachineShellPublicKeyType.RSA;
  private static final int SOME_PUBLIC_KEY_SIZE = 1024;

  private static final int SOME_DIVIDEND = 9;
  private static final int SOME_DIVISOR = 4;
  private static final int SOME_DIVISION = 2;

  @SuppressWarnings("resource")
  @Test
  void executeCommandSuccessfully(
      @Mock CallbackHandler callbackHandler,
      @TempDir File configDir) throws Exception {
    MachineShellClientContext context = new MachineShellClientContext(callbackHandler);
    try (OutputStream publicKeyOutput = new FileOutputStream(new File(
        configDir,
        "public-key.pem"))) {
      context.generateIdentity(SOME_IDENTITY_NAME, new MachineShellPublicKey(
          SOME_PUBLIC_KEY_TYPE,
          SOME_PUBLIC_KEY_SIZE,
          publicKeyOutput));
    }
    GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(
        CONTAINER_IMAGE_NAME))
            .withExposedPorts(2222)
            .withEnv("PUID", "1000")
            .withEnv("PGID", "1000")
            .withEnv("TZ", "Etc/UTC")
            .withEnv("PASSWORD_ACCESS", "true")
            .withEnv("PUBLIC_KEY_FILE", "/config/public-key.pem")
            .withEnv("USER_NAME", KNOWN_USERNAME)
            .withFileSystemBind(configDir.getAbsolutePath(), "/config");
    container.start();
    doAnswer(invocation -> {
      Stream.of(invocation.getArgument(0, Callback[].class))
          .filter(MachineShellHostCallback.class::isInstance)
          .map(MachineShellHostCallback.class::cast)
          .filter(callback -> getByName(container.getHost()).equals(callback.getAddress()))
          .findAny()
          .ifPresent(callback -> callback.setStatus(KNOWN));
      return null;
    }).when(callbackHandler).handle(any());

    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {

      int result = connection.execute(new TestDivideCommand(SOME_DIVIDEND, SOME_DIVISOR));

      assertThat(result).isEqualTo(SOME_DIVISION);
    }

    container.stop();
  }
}
