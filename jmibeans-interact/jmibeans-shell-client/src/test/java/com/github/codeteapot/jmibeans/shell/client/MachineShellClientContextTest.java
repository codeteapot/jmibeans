package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKeyType.RSA;
import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.nio.file.Files.copy;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Handler;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import com.github.codeteapot.jmibeans.shell.client.secutity.auth.MachineShellIdentityName;
import com.github.codeteapot.testing.logging.LoggerStub;

@Tag("integration")
@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension.class)
class MachineShellClientContextTest {

  private static final int ANY_DIVIDEND = 1;
  private static final int ANY_DIVISOR = 1;

  private static final String CONTAINER_IMAGE_NAME = "lscr.io/linuxserver/openssh-server:latest";

  private static final int FILE_TEST_BUFFER_SIZE = 64;

  private static final boolean PREMATURE_OUTPUT_CLOSING = true;

  private static final MachineShellIdentityName KNOWN_IDENTITY_NAME =
      new MachineShellIdentityName("known-identity");
  private static final MachineShellIdentityName UNKNOWN_IDENTITY_NAME =
      new MachineShellIdentityName("unknown-identity");

  private static final String KNOWN_USERNAME = "scott";
  private static final char[] KNOWN_PASSWORD = {'1', '2', '3', '4', '5', '6', '7', '8'};
  private static final String KNOWN_PASSWORD_STR = "12345678";

  private static final int ZERO_DIVISOR = 0;

  private static final MachineShellIdentityName SOME_IDENTITY_NAME =
      new MachineShellIdentityName("some-identity");

  private static final MachineShellPublicKeyType SOME_PUBLIC_KEY_TYPE =
      MachineShellPublicKeyType.RSA;
  private static final int SOME_PUBLIC_KEY_SIZE = 1024;

  private static final int SOME_DIVIDEND = 9;
  private static final int SOME_DIVISOR = 4;
  private static final int SOME_DIVISION = 2;

  private static final long LARGE_DELAY_MILLIS = 2100L;
  private static final long SHORT_EXECUTION_TIMEOUT_MILLIS = 2000L;
  private static final long ENOUGH_EXECUTION_TIMEOUT_MILLIS = 2200L;

  private static final String SHARED_FILE_PATH = "/config/shared-file.txt";

  private static final byte[] SOME_SOURCE_FILE_CONTENT = {0x41, 0x42, 0x43, 0x44};
  private static final byte[] SOME_TARGET_FILE_CONTENT = {0x51, 0x52, 0x53, 0x54};

  private static final byte[] SOME_PUBLIC_KEY_CONTENT = {0x01, 0x02, 0x03};

  private static final Exception SOME_IMPLEMENTATION_EXCEPTION = new Exception();

  private GenericContainer<?> container;

  private File sharedFile;

  @Mock
  private Supplier<Long> executionTimeoutMillisSupplier;

  private TestMachineShellClientContextCallbackHandler callbackHandler;
  private JschMachineShellClientImplementationContext implementation;
  private Set<JschKeyPairIdentity> identities;
  private JschKeyPairIdentity knownIdentity;
  private JschKeyPairIdentity unknownIdentity;

  @Mock
  private JschKeyPairIdentityConstructor identityConstructor;

  private MachineShellClientContext context;

  @SuppressWarnings("resource")
  @BeforeAll
  void init(@TempDir File configDir) throws Exception {
    callbackHandler = new TestMachineShellClientContextCallbackHandler();
    implementation = new JschMachineShellClientImplementationContext(callbackHandler);
    knownIdentity = new JschKeyPairIdentity(implementation, KNOWN_IDENTITY_NAME, RSA, 1024);
    unknownIdentity = new JschKeyPairIdentity(implementation, UNKNOWN_IDENTITY_NAME, RSA, 1024);
    try (OutputStream output = new FileOutputStream(new File(configDir, "public-key.pem"))) {
      knownIdentity.writePublicKey(output);
    }
    sharedFile = new File(configDir, "shared-file.txt");
    sharedFile.createNewFile();
    container = new GenericContainer<>(DockerImageName.parse(CONTAINER_IMAGE_NAME))
        .withExposedPorts(2222) // Not the default port
        .withEnv("PUID", "1000")
        .withEnv("PGID", "1000")
        .withEnv("TZ", "Etc/UTC")
        .withEnv("PASSWORD_ACCESS", "true")
        .withEnv("PUBLIC_KEY_FILE", "/config/public-key.pem")
        .withEnv("USER_NAME", KNOWN_USERNAME)
        .withEnv("USER_PASSWORD", KNOWN_PASSWORD_STR)
        .withFileSystemBind(configDir.getAbsolutePath(), "/config");
    container.start();
  }

  @AfterAll
  void destroy() {
    container.stop();
  }

  @BeforeEach
  void setUp() throws Exception {
    identities = new HashSet<>();
    context = new MachineShellClientContext(
        implementation,
        identities,
        executionTimeoutMillisSupplier,
        identityConstructor);
  }

  @AfterEach
  void tearDown() {
    callbackHandler.reset();
    identities.clear();
  }

  @Test
  void executeCommandWithIdentity(
      @Mock MachineShellClientConnectionListener someConnectionListener) throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {
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
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME));
    connection.addConnectionEventListener(someConnectionListener);
    container.stop();

    Throwable e = catchThrowable(() -> connection.execute(new TestDivideCommand(
        ANY_DIVIDEND,
        ANY_DIVISOR)));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCauseInstanceOf(Exception.class);
    container.start();
    verify(someConnectionListener)
        .connectionErrorOccurred(argThat(event -> event.getClientException()
            .map(clientException -> clientException.getCause() instanceof Exception)
            .orElse(false)));
  }

  @Test
  void failWhenExecutingCommandWithUnderlyingConnectionError(
      @Mock MachineShellClientConnectionListener someConnectionListener) throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME));
    connection.addConnectionEventListener(someConnectionListener);

    Throwable e = catchThrowable(() -> connection.execute(new TestDivideCommand(
        ANY_DIVIDEND,
        ANY_DIVISOR,
        container::stop)));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCauseInstanceOf(Exception.class);
    container.start();
    verify(someConnectionListener)
        .connectionErrorOccurred(argThat(event -> event.getClientException()
            .map(clientException -> clientException.getCause() instanceof Exception)
            .orElse(false)));
  }

  @Test
  void failWhenExecutingCommandWithTimeout(
      @Mock MachineShellClientConnectionListener someConnectionListener) throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(SHORT_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {
      connection.addConnectionEventListener(someConnectionListener);

      Throwable e = catchThrowable(() -> connection.execute(new TestDivideCommand(
          ANY_DIVIDEND,
          ANY_DIVISOR,
          LARGE_DELAY_MILLIS)));

      assertThat(e)
          .isInstanceOf(MachineShellClientCommandExecutionException.class)
          .hasMessageContaining("Timeout");
    }
    verify(someConnectionListener, never()).connectionErrorOccurred(any());
  }

  @Test
  void failWhenExecutingCommandWithExecutionError(
      @Mock MachineShellClientConnectionListener someConnectionListener) throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {
      connection.addConnectionEventListener(someConnectionListener);

      Throwable e = catchThrowable(() -> connection.execute(new TestDivideCommand(
          ANY_DIVIDEND,
          ZERO_DIVISOR)));

      assertThat(e)
          .isInstanceOf(MachineShellClientCommandExecutionException.class)
          .hasCauseInstanceOf(Exception.class);
    }
    verify(someConnectionListener, never()).connectionErrorOccurred(any());
  }

  @Test
  void failWhenExecutingCommandWithStreamError(
      @Mock MachineShellClientConnectionListener someConnectionListener) throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {
      connection.addConnectionEventListener(someConnectionListener);

      Throwable e = catchThrowable(() -> connection.execute(new TestDivideCommand(
          ANY_DIVIDEND,
          ANY_DIVISOR,
          PREMATURE_OUTPUT_CLOSING)));

      assertThat(e)
          .isInstanceOf(MachineShellClientCommandExecutionException.class)
          .hasCauseInstanceOf(IOException.class);
    }
    verify(someConnectionListener, never()).connectionErrorOccurred(any());
  }

  @Test
  void executeCommandWithPassword() throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME,
            KNOWN_PASSWORD))) {
      int result = connection.execute(new TestDivideCommand(SOME_DIVIDEND, SOME_DIVISOR));

      assertThat(result).isEqualTo(SOME_DIVISION);
    }
  }

  @Test
  void readFromSharedFile() throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    try (InputStream input = new ByteArrayInputStream(SOME_SOURCE_FILE_CONTENT)) {
      copy(input, sharedFile.toPath(), REPLACE_EXISTING);
    }
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {
      MachineShellClientFile file = connection.file(SHARED_FILE_PATH);

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
  void failWhenReadingFromUnaccessibleSharedFile() throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {
      sharedFile.setReadable(false);
      MachineShellClientFile file = connection.file(SHARED_FILE_PATH);

      Throwable e = catchThrowable(() -> file.getInputStream());

      assertThat(e).isInstanceOf(IOException.class);
      sharedFile.setReadable(true);
    }
  }

  @Test
  void failWhenReadingFromDetachedSharedFile() throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME));
    MachineShellClientFile file = connection.file(SHARED_FILE_PATH);
    connection.close();

    Throwable e = catchThrowable(() -> file.getInputStream());

    assertThat(e).isInstanceOf(IOException.class);
  }

  @Test
  void writeToSharedFile() throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    try (InputStream input = new ByteArrayInputStream(new byte[0])) {
      copy(input, sharedFile.toPath(), REPLACE_EXISTING);
    }
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {
      MachineShellClientFile file = connection.file(SHARED_FILE_PATH);

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
  void failWhenWritingToUnaccessibleSharedFile() throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);

    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {
      sharedFile.setWritable(false);
      MachineShellClientFile file = connection.file(SHARED_FILE_PATH);

      Throwable e = catchThrowable(() -> file.getOutputStream());

      assertThat(e).isInstanceOf(IOException.class);
      sharedFile.setWritable(true);
    }
  }

  @Test
  void failWhenWritingToDetachedSharedFile() throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);

    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME));
    MachineShellClientFile file = connection.file(SHARED_FILE_PATH);
    connection.close();
    Throwable e = catchThrowable(() -> file.getOutputStream());

    assertThat(e).isInstanceOf(IOException.class);
  }

  @Test
  void failWhenAccessingSharedFileWithConnectionError(
      @Mock MachineShellClientConnectionListener someConnectionListener) throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME));
    connection.addConnectionEventListener(someConnectionListener);
    container.stop();

    Throwable e = catchThrowable(() -> connection.file(SHARED_FILE_PATH));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCauseInstanceOf(Exception.class);
    container.start();
  }

  @Test
  void logWarningWhenDetachingAlreadyDetachedSharedFile(
      @Mock MachineShellClientFileStateChanger anyStateChanger,
      @Mock Handler someLoggerHandler) throws Exception {
    LoggerStub loggerStub = loggerStubFor(
        MachineShellClientFileDetachedState.class.getName(),
        someLoggerHandler);
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME));
    MachineShellClientFile file = connection.file(SHARED_FILE_PATH);
    connection.close();

    // Get implementation hack
    JschMachineShellClientFile jschFile = (JschMachineShellClientFile) file;
    jschFile.detach();

    verify(someLoggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)));
    loggerStub.restore();
  }

  @Test
  void ignoreRemovedConnextionListener(
      @Mock MachineShellClientConnectionListener someConnectionListener) throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    try (MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME))) {
      connection.addConnectionEventListener(someConnectionListener);

      connection.removeConnectionEventListener(someConnectionListener);
    }
    verify(someConnectionListener, never()).connectionClosed(any());
  }

  @Test
  void failWhenExecutingCommandThroughUnavailableConnection() throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME));
    connection.close();

    Throwable e = catchThrowable(() -> connection.execute(new TestDivideCommand(
        ANY_DIVIDEND,
        ANY_DIVISOR)));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void failWhenAccessingSharedFileThroughUnavailableConnection() throws Exception {
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME));
    connection.close();

    Throwable e = catchThrowable(() -> connection.file(SHARED_FILE_PATH));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void logWarningWhenClosingUnavailableConnection(@Mock Handler someLoggerHandler)
      throws Exception {
    LoggerStub loggerStub = loggerStubFor(
        MachineShellClientConnectionUnavailableState.class.getName(),
        someLoggerHandler);
    when(executionTimeoutMillisSupplier.get())
        .thenReturn(ENOUGH_EXECUTION_TIMEOUT_MILLIS);
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    MachineShellClientConnection connection = context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME));
    connection.close();

    connection.close();

    verify(someLoggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)));
    loggerStub.restore();
  }

  @Test
  void failWhenHostIsNotAllowed() {
    identities.add(knownIdentity);

    Throwable e = catchThrowable(() -> context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME)));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void failWhenAuthenticationHasFailed() {
    callbackHandler.addAllowedHost(container.getHost());

    Throwable e = catchThrowable(() -> context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME)));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void failWhenUsingDefaultPortButItIsNotAvailable() {
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);

    Throwable e = catchThrowable(() -> context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            KNOWN_USERNAME)));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void failWhenAuthenticationHasFailedBecauseIdentityOnly() {
    callbackHandler.addAllowedHost(container.getHost());
    identities.add(knownIdentity);
    identities.add(unknownIdentity);

    Throwable e = catchThrowable(() -> context.getConnection(
        new TestMachineShellClientContextConnectionSpec(
            container.getHost(),
            container.getFirstMappedPort(),
            KNOWN_USERNAME,
            UNKNOWN_IDENTITY_NAME)));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void generateIdentity(@Mock JschKeyPairIdentity someIdentity) throws Exception {
    try (ByteArrayOutputStream somePublicKeyOutput = new ByteArrayOutputStream()) {
      when(identityConstructor.construct(
          implementation,
          SOME_IDENTITY_NAME,
          SOME_PUBLIC_KEY_TYPE,
          SOME_PUBLIC_KEY_SIZE)).thenReturn(someIdentity);
      doAnswer(invocation -> {
        OutputStream publicKeyOutput = invocation.getArgument(0);
        publicKeyOutput.write(SOME_PUBLIC_KEY_CONTENT);
        publicKeyOutput.flush();
        return null;
      }).when(someIdentity).writePublicKey(somePublicKeyOutput);

      context.generateIdentity(SOME_IDENTITY_NAME, new MachineShellPublicKey(
          SOME_PUBLIC_KEY_TYPE,
          SOME_PUBLIC_KEY_SIZE,
          somePublicKeyOutput));

      assertThat(somePublicKeyOutput.toByteArray()).isEqualTo(SOME_PUBLIC_KEY_CONTENT);
    }
  }

  @Test
  void failWhileGeneratingIdentity(@Mock OutputStream anyOutput) throws Exception {
    when(identityConstructor.construct(
        implementation,
        SOME_IDENTITY_NAME,
        SOME_PUBLIC_KEY_TYPE,
        SOME_PUBLIC_KEY_SIZE)).thenThrow(SOME_IMPLEMENTATION_EXCEPTION);

    Throwable e = catchThrowable(() -> context.generateIdentity(
        SOME_IDENTITY_NAME,
        new MachineShellPublicKey(SOME_PUBLIC_KEY_TYPE, SOME_PUBLIC_KEY_SIZE, anyOutput)));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCause(SOME_IMPLEMENTATION_EXCEPTION);
  }
}
