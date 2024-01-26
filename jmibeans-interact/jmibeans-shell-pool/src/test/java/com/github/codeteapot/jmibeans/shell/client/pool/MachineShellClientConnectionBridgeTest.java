package com.github.codeteapot.jmibeans.shell.client.pool;

import static com.github.codeteapot.testing.logging.hamcrest.SomeLogRecordMatcher.someLogRecord;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.logging.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;
import com.github.codeteapot.testing.logging.junit.jupiter.LoggingExtension;
import com.github.codeteapot.testing.logging.mockito.MockLogger;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
class MachineShellClientConnectionBridgeTest {

  private static final Duration ENOUGH_TIMEOUT = ofMillis(2400L);

  private static final Duration LITTLE_TIMEOUT = ofMillis(800L);

  private static final String SOME_FILE_PATH = "/some/file";

  private static final Object SOME_EXECUTION_RESULT = new Object();

  private static final Exception SOME_EXCEPTION = new Exception();

  @MockLogger(
      name = "com.github.codeteapot.jmibeans.shell.client.pool.MachineShellClientConnectionBridge")
  private Handler loggerHandler;

  @Mock
  private Supplier<Duration> idleTimeoutSupplier;

  @Mock
  private MachineShellClientConnection connection;

  private MachineShellClientConnectionBridge bridge;

  @BeforeEach
  void setUp() {
    bridge = new MachineShellClientConnectionBridge(
        newScheduledThreadPool(1),
        idleTimeoutSupplier,
        connection);
  }

  @Test
  void executeCommand(@Mock MachineShellClientCommand<Object> someCommand) throws Exception {
    when(connection.execute(someCommand)).thenReturn(SOME_EXECUTION_RESULT);

    Object result = bridge.execute(someCommand);

    assertThat(result).isEqualTo(SOME_EXECUTION_RESULT);
  }

  @Test
  void accessFile(@Mock MachineShellClientFile someFile) throws Exception {
    when(connection.file(SOME_FILE_PATH)).thenReturn(someFile);

    MachineShellClientFile file = bridge.file(SOME_FILE_PATH);

    assertThat(file).isEqualTo(someFile);
  }

  @Test
  void closeNowSuccessfully() throws Exception {
    bridge.closeNow();

    verify(connection).close();
  }

  @Test
  void logWarningWhenCloseFails() throws Exception {
    doThrow(SOME_EXCEPTION).when(connection).close();

    bridge.closeNow();

    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withThrown(equalTo(SOME_EXCEPTION))));
  }

  @Test
  void closeIdleTimeoutCompleted(@Mock Callable<Void> someBeforeCloseAction) {
    when(idleTimeoutSupplier.get()).thenReturn(LITTLE_TIMEOUT);

    bridge.closeIdleTimeout(someBeforeCloseAction);

    await().timeout(ENOUGH_TIMEOUT).untilAsserted(() -> {
      InOrder order = inOrder(someBeforeCloseAction, connection);
      order.verify(someBeforeCloseAction).call();
      order.verify(connection).close();
    });
  }

  @Test
  void closeIdleTimeoutCancelBeforeBegin(@Mock Callable<Void> someBeforeCloseAction)
      throws Exception {
    when(idleTimeoutSupplier.get()).thenReturn(LITTLE_TIMEOUT);

    MachineShellClientConnectionBridgeCloseTask task = bridge.closeIdleTimeout(
        someBeforeCloseAction);
    task.cancel();

    verify(someBeforeCloseAction, never()).call();
    verify(connection, never()).close();
  }
}
