package com.github.codeteapot.jmibeans.shell.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import com.github.codeteapot.jmibeans.shell.client.secutity.auth.MachineShellIdentityName;

@Tag("integration")
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
  void executeCommandSuccessfully(@TempDir File configDir) throws Exception {
    TestMachineShellClientContextCallbackHandler callbackHandler =
        new TestMachineShellClientContextCallbackHandler();
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
    callbackHandler.addAllowedHost(container.getHost());

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
