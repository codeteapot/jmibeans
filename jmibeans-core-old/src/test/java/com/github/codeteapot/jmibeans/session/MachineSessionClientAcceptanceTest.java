package com.github.codeteapot.jmibeans.session;

import static com.github.codeteapot.testing.InetAddressCreator.getByName;
import static java.nio.file.Files.copy;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

@Tag("integration")
@TestInstance(PER_CLASS)
public class MachineSessionClientAcceptanceTest {

  private static final int ANY_DIVIDEND = 1;
  private static final int ANY_DIVISOR = 1;

  private static final String CONTAINER_IMAGE_NAME = "lscr.io/linuxserver/openssh-server:latest";

  private static final int FILE_TEST_BUFFER_SIZE = 64;

  private static final Integer NULL_PORT = null;

  private static final String EXECUTION_TIMEOUT_MESSAGE = "Execution timeout";

  private static final long SHORT_EXECUTION_TIMEOUT_MILLIS = 2000L;

  private static final String TEST_USERNAME = "scott";
  private static final byte[] TEST_PASSWORD = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38};
  private static final String TEST_PASSWORD_STR = "12345678";

  private static final MachineSessionPasswordName TEST_PASSWORD_NAME =
      new MachineSessionPasswordName("test-password");

  private static final int ZERO_DIVISOR = 0;
  private static final String DIVISION_BY_ZERO_MESSAGE = "division by zero";

  private static final int SOME_DIVIDEND = 9;
  private static final int SOME_DIVISOR = 4;
  private static final int SOME_DIVISION = 2;

  private static final long LARGE_DELAY_MILLIS = 2100L;

  private static final String SHARED_FILE_PATH = "/config/shared-file.txt";

  private static final byte[] SOME_SOURCE_FILE_CONTENT = {0x41, 0x42, 0x43, 0x44};
  private static final byte[] SOME_TARGET_FILE_CONTENT = {0x51, 0x52, 0x53, 0x54};

  private JSch jsch;

  private SSHCredentialRepository containerCredentialRepository;

  private GenericContainer<?> container;
  private InetAddress containerHost;
  private int containerPort;

  private File sharedFile;

  @SuppressWarnings("resource")
  @BeforeAll
  public void init(@TempDir File configDir) throws Exception {
    JSch.setConfig("StrictHostKeyChecking", "no"); // TODO Do it right

    jsch = spy(new JSch());
    containerCredentialRepository = new SSHCredentialRepository(jsch);

    File publicKeyFile = new File(configDir, "public-key.pem");
    publicKeyFile.createNewFile();
    try (OutputStream publicKeyOutput = new FileOutputStream(publicKeyFile)) {
      containerCredentialRepository.generateKeyPair(publicKeyOutput);
    }

    sharedFile = new File(configDir, "shared-file.txt");
    sharedFile.createNewFile();

    container = new GenericContainer<>(DockerImageName.parse(CONTAINER_IMAGE_NAME))
        .withExposedPorts(2222)
        .withEnv("PUID", "1000")
        .withEnv("PGID", "1000")
        .withEnv("TZ", "Etc/UTC")
        .withEnv("PASSWORD_ACCESS", "true")
        .withEnv("PUBLIC_KEY_FILE", "/config/public-key.pem")
        .withEnv("USER_NAME", TEST_USERNAME)
        .withEnv("USER_PASSWORD", TEST_PASSWORD_STR)
        .withFileSystemBind(configDir.getAbsolutePath(), "/config");
    container.start();
  }

  @AfterAll
  public void destroy() {
    container.stop();
  }

  @BeforeEach
  public void setUp() {
    reset(jsch);
    containerHost = getByName(container.getHost());
    containerPort = container.getFirstMappedPort();
  }

  @Test
  public void executeCommandWithPassword() throws Exception {
    MachineSessionClient client = new MachineSessionClient();
    client.addPassword(TEST_PASSWORD_NAME, TEST_PASSWORD);

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(singleton(TEST_PASSWORD_NAME)))) {

      int result = session.execute(new TestDivideCommand(SOME_DIVIDEND, SOME_DIVISOR));

      assertThat(result).isEqualTo(SOME_DIVISION);
    }
  }

  @Test
  public void executeCommand() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {

      int result = session.execute(new TestDivideCommand(SOME_DIVIDEND, SOME_DIVISOR));

      assertThat(result).isEqualTo(SOME_DIVISION);
    }
  }

  @Test
  public void executeCommandWithError() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {

      Throwable e = catchThrowable(() -> session.execute(new TestDivideCommand(
          ANY_DIVIDEND,
          ZERO_DIVISOR)));

      assertThat(e)
          .isInstanceOf(MachineCommandExecutionException.class)
          .hasRootCauseMessage(DIVISION_BY_ZERO_MESSAGE);
    }
  }

  @Test
  public void executeCommandWithTimeout() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SHORT_EXECUTION_TIMEOUT_MILLIS,
        SSHMachineSession::new);

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {

      Throwable e = catchThrowable(() -> session.execute(new TestDivideCommand(
          ANY_DIVIDEND,
          ANY_DIVISOR,
          LARGE_DELAY_MILLIS)));

      assertThat(e)
          .isInstanceOf(MachineCommandExecutionException.class)
          .hasRootCauseMessage(EXECUTION_TIMEOUT_MESSAGE);
    }
  }

  @Test
  public void executeCommandWithConnectionError() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    container.stop();

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {

      Throwable e = catchThrowable(() -> session.execute(new TestDivideCommand(
          ANY_DIVIDEND,
          ANY_DIVISOR)));

      assertThat(e)
          .isInstanceOf(MachineSessionException.class)
          .hasCauseInstanceOf(JSchException.class);

      container.start();
    }
  }

  @Test
  public void executeCommandWithUnderlyingConnectionError() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {

      Throwable e = catchThrowable(() -> session.execute(new TestDivideCommand(
          ANY_DIVIDEND,
          ANY_DIVISOR,
          container::stop)));

      assertThat(e)
          .isInstanceOf(MachineSessionException.class)
          .hasCauseInstanceOf(JSchException.class);

      container.start();
    }
  }

  @Test
  public void executeCommandWithDefaultPort() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    try (MachineSession session = client.getSession(
        containerHost,
        NULL_PORT,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {

      // Assuming that mapped port won't be the same as the default (Expected to be 22)
      Throwable e = catchThrowable(() -> session.execute(new TestDivideCommand(
          ANY_DIVIDEND,
          ANY_DIVISOR)));

      assertThat(e)
          .isInstanceOf(MachineSessionException.class)
          .hasCauseInstanceOf(JSchException.class);
    }
  }

  @Test
  public void executeCommandAfterSessionClosed() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()));

    session.close();

    Throwable e = catchThrowable(() -> session.execute(new TestDivideCommand(
        ANY_DIVIDEND,
        ANY_DIVISOR)));

    assertThat(e).isInstanceOf(MachineSessionException.class);
  }

  @Test
  public void readFromSharedFile() throws Exception {
    copy(new ByteArrayInputStream(SOME_SOURCE_FILE_CONTENT), sharedFile.toPath(), REPLACE_EXISTING);

    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {
      MachineSessionFile file = session.file(SHARED_FILE_PATH);
      try (
          InputStream input = file.getInputStream();
          ByteArrayOutputStream result = new ByteArrayOutputStream()) {
        byte[] b = new byte[FILE_TEST_BUFFER_SIZE];
        int len = input.read(b, 0, FILE_TEST_BUFFER_SIZE);
        while (len > 0) {
          result.write(b, 0, len);
          len = input.read(b, 0, FILE_TEST_BUFFER_SIZE);
        }

        assertThat(result.toByteArray()).isEqualTo(SOME_SOURCE_FILE_CONTENT);
      }
    }
  }

  @Test
  public void readFromSharedFileWithUnderlyingConnectionError() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {
      MachineSessionFile file = session.file(SHARED_FILE_PATH);

      container.stop();

      Throwable e = catchThrowable(() -> file.getInputStream());

      assertThat(e).isInstanceOf(IOException.class);

      container.start();
    }
  }

  @Test
  public void readFromSharedFileAfterSessionClosed() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()));

    MachineSessionFile file = session.file(SHARED_FILE_PATH);
    session.close();

    Throwable e = catchThrowable(() -> file.getInputStream());

    assertThat(e).isInstanceOf(IOException.class);
  }

  @Test
  public void writeToSharedFile() throws Exception {
    copy(new ByteArrayInputStream(new byte[0]), sharedFile.toPath(), REPLACE_EXISTING);

    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {
      MachineSessionFile file = session.file(SHARED_FILE_PATH);
      try (OutputStream output = file.getOutputStream()) {
        output.write(SOME_TARGET_FILE_CONTENT);
      }
    }
    try (
        InputStream input = new FileInputStream(sharedFile);
        ByteArrayOutputStream result = new ByteArrayOutputStream()) {
      byte[] b = new byte[FILE_TEST_BUFFER_SIZE];
      int len = input.read(b, 0, FILE_TEST_BUFFER_SIZE);
      while (len > 0) {
        result.write(b, 0, len);
        len = input.read(b, 0, FILE_TEST_BUFFER_SIZE);
      }

      assertThat(result.toByteArray()).isEqualTo(SOME_TARGET_FILE_CONTENT);
    }
  }

  @Test
  public void writeToSharedFileWithUnderlyingConnectionError() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {
      MachineSessionFile file = session.file(SHARED_FILE_PATH);

      container.stop();

      Throwable e = catchThrowable(() -> file.getOutputStream());

      assertThat(e).isInstanceOf(IOException.class);

      container.start();
    }
  }

  @Test
  public void writeToSharedFileAfterSessionClosed() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()));

    MachineSessionFile file = session.file(SHARED_FILE_PATH);
    session.close();

    Throwable e = catchThrowable(() -> file.getOutputStream());

    assertThat(e).isInstanceOf(IOException.class);
  }

  @Test
  public void accessSharedFileWithConnectionError() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    container.stop();

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {

      Throwable e = catchThrowable(() -> session.file(SHARED_FILE_PATH));

      assertThat(e)
          .isInstanceOf(MachineSessionException.class)
          .hasCauseInstanceOf(JSchException.class);

      container.start();
    }
  }

  @Test
  public void accessSharedFileAfterSessionClosed() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()));
    session.close();

    Throwable e = catchThrowable(() -> session.file(SHARED_FILE_PATH));

    assertThat(e).isInstanceOf(MachineSessionException.class);
  }

  @Test
  public void failWhenClosingSessionTwice() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()));
    session.close();

    Throwable e = catchThrowable(() -> session.close());

    assertThat(e).isInstanceOf(IOException.class);
  }

  @Test
  public void lazilyGetUnderlyingSession() throws Exception {
    MachineSessionClient client = new MachineSessionClient(
        jsch,
        containerCredentialRepository,
        SSHMachineSession::new);

    verify(jsch, never()).getSession(anyString(), anyString());
    verify(jsch, never()).getSession(anyString(), anyString(), anyInt());

    try (MachineSession session = client.getSession(
        containerHost,
        containerPort,
        TEST_USERNAME,
        new TestMachineSessionAuthentication(emptySet()))) {

      session.execute(new TestDivideCommand(ANY_DIVIDEND, ANY_DIVISOR));
      session.execute(new TestDivideCommand(ANY_DIVIDEND, ANY_DIVISOR));

      // Using non default port
      verify(jsch, times(1)).getSession(anyString(), anyString(), anyInt());
    }
  }
}
