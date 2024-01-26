package com.github.codeteapot.jmibeans.shell.client.pool;

import static com.github.codeteapot.testing.logging.hamcrest.SomeLogRecordMatcher.someLogRecord;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnectionEvent;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;
import com.github.codeteapot.testing.logging.junit.jupiter.LoggingExtension;
import com.github.codeteapot.testing.logging.mockito.MockLogger;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Handler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
class PooledMachineShellConnectionTest {

  private static final String ANY_USERNAME = "nobody";
  private static final String ANY_FILE_PATH = "/any/file";

  private static final MachineShellClientException ANY_CLIENT_EXCEPTION =
      new MachineShellClientException(new Exception());

  private static final String SOME_USERNAME = "scott";
  private static final String SOME_FILE_PATH = "/some/file";

  private static final String ANOTHER_USERNAME = "jessie";

  private static final Object SOME_EXECUTION_RESULT = new Object();

  private static final MachineShellClientException SOME_CLIENT_EXCEPTION =
      new MachineShellClientException(new Exception());

  @Mock
  private Consumer<PooledMachineShellClientConnection> removeAction;

  @Mock
  private MachineShellConnectionConstructor connectionConstructor;

  @MockLogger(
      name = "com.github.codeteapot.jmibeans.shell.client.pool.PooledMachineShellClientConnection")
  private Handler loggerHandler;

  @Test
  void getShellConnection(
      @Mock PooledMachineShellClientConnectionState anyState,
      @Mock MachineShellConnection someShellConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> anyState);
    when(connectionConstructor.construct(connection)).thenReturn(someShellConnection);

    MachineShellConnection shellConnection = connection.getConnection();

    assertThat(shellConnection).isEqualTo(someShellConnection);
  }

  @Test
  void cannotRequestWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            anyStateChanger,
            anyBridge,
            ANY_USERNAME));

    Throwable e = catchThrowable(() -> connection.request(ANY_USERNAME));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotAcquireWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            anyStateChanger,
            anyBridge,
            ANY_USERNAME));

    Throwable e = catchThrowable(() -> connection.acquire());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void executeCommandThroughBridgeWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge someBridge,
      @Mock MachineShellClientCommand<Object> someCommand) throws Exception {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            anyStateChanger,
            someBridge,
            ANY_USERNAME));
    when(someBridge.execute(someCommand)).thenReturn(SOME_EXECUTION_RESULT);

    Object result = connection.execute(someCommand);

    assertThat(result).isEqualTo(SOME_EXECUTION_RESULT);
  }

  @Test
  void accessFileThroughBridgeWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge someBridge,
      @Mock MachineShellClientFile someFile) throws Exception {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            anyStateChanger,
            someBridge,
            ANY_USERNAME));
    when(someBridge.file(SOME_FILE_PATH)).thenReturn(someFile);

    MachineShellClientFile file = connection.file(SOME_FILE_PATH);

    assertThat(file).isEqualTo(someFile);
  }

  @Test
  void closeNowThroughBridgeWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge someBridge) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            someStateChanger,
            someBridge,
            ANY_USERNAME));

    connection.closeNow();

    InOrder order = inOrder(someStateChanger, someBridge);
    order.verify(someStateChanger).closing();
    order.verify(someBridge).closeNow();
  }

  @Test
  void releaseAndCloseWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge someBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask someCloseTask) throws Exception {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            someStateChanger,
            someBridge,
            SOME_USERNAME));
    AtomicReference<Callable<?>> closeTaskAction = new AtomicReference<>();
    when(someBridge.closeIdleTimeout(any())).thenAnswer(invocation -> {
      closeTaskAction.set(invocation.getArgument(0));
      return someCloseTask;
    });

    connection.release();

    verify(someStateChanger).available(someBridge, SOME_USERNAME, someCloseTask);
    reset(someStateChanger);
    closeTaskAction.get().call();
    verify(someStateChanger).closing();
  }

  @Test
  void onConnectionClosedWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME));

    connection.connectionClosed(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).closed(
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent()));
  }

  @Test
  void onConnectionClosedWithExceptionWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME));

    connection.connectionClosed(new MachineShellClientConnectionEvent(
        someClientConnection,
        SOME_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).closed(argThat(exceptionSupplier -> exceptionSupplier.get()
        .map(SOME_CLIENT_EXCEPTION::equals)
        .orElse(false)));
  }

  @Test
  void onErrorOccurredWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).errorOccurred(
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent()));
  }

  @Test
  void onErrorOccurredWithExceptionWhenAcquired(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAcquiredState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(
        someClientConnection,
        SOME_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).errorOccurred(argThat(exceptionSupplier -> exceptionSupplier.get()
        .map(SOME_CLIENT_EXCEPTION::equals)
        .orElse(false)));
  }

  @Test
  void requestMatchingUsernameWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge someBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask someCloseTask) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            someStateChanger,
            someBridge,
            SOME_USERNAME,
            someCloseTask));

    boolean available = connection.request(SOME_USERNAME);

    assertThat(available).isTrue();
    InOrder order = inOrder(someStateChanger, someCloseTask);
    order.verify(someCloseTask).cancel();
    order.verify(someStateChanger).requested(someBridge, SOME_USERNAME);
  }

  @Test
  void requestUnmatchingUsernameWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            someStateChanger,
            anyBridge,
            SOME_USERNAME,
            anyCloseTask));

    boolean available = connection.request(ANOTHER_USERNAME);

    assertThat(available).isFalse();
    verifyNoInteractions(someStateChanger);
  }

  @Test
  void cannotAcquireWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME,
            anyCloseTask));

    Throwable e = catchThrowable(() -> connection.acquire());

    assertThat(e).isInstanceOf(IllegalStateException.class);
    verifyNoInteractions(someStateChanger);
  }

  @Test
  void failWhenExecutingCommandWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask,
      @Mock MachineShellClientCommand<?> anyCommand) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            anyStateChanger,
            anyBridge,
            ANY_USERNAME,
            anyCloseTask));

    Throwable e = catchThrowable(() -> connection.execute(anyCommand));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void failWhenAccessingFileWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            anyStateChanger,
            anyBridge,
            ANY_USERNAME,
            anyCloseTask));

    Throwable e = catchThrowable(() -> connection.file(ANY_FILE_PATH));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void closeNowThroughBridgeWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge someBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            someStateChanger,
            someBridge,
            ANY_USERNAME,
            anyCloseTask));

    connection.closeNow();

    InOrder order = inOrder(someStateChanger, someBridge);
    order.verify(someStateChanger).closing();
    order.verify(someBridge).closeNow();
  }

  @Test
  void cannotReleaseWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            anyStateChanger,
            anyBridge,
            ANY_USERNAME,
            anyCloseTask));

    Throwable e = catchThrowable(() -> connection.release());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void onConnectionClosedWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME,
            anyCloseTask));

    connection.connectionClosed(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).closed(
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent()));
  }

  @Test
  void onConnectionClosedWithExceptionWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME,
            anyCloseTask));

    connection.connectionClosed(new MachineShellClientConnectionEvent(
        someClientConnection,
        SOME_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).closed(argThat(exceptionSupplier -> exceptionSupplier.get()
        .map(SOME_CLIENT_EXCEPTION::equals)
        .orElse(false)));
  }

  @Test
  void onErrorOccurredWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME,
            anyCloseTask));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).errorOccurred(
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent()));
  }

  @Test
  void onErrorOccurredWithExceptionWhenAvailable(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnectionBridgeCloseTask anyCloseTask,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionAvailableState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME,
            anyCloseTask));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(
        someClientConnection,
        SOME_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).errorOccurred(argThat(exceptionSupplier -> exceptionSupplier.get()
        .map(SOME_CLIENT_EXCEPTION::equals)
        .orElse(false)));
  }

  @Test
  void cannotRequestWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            anyExceptionSupplier));

    Throwable e = catchThrowable(() -> connection.request(ANY_USERNAME));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotAcquireWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            anyExceptionSupplier));

    Throwable e = catchThrowable(() -> connection.acquire());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void failWhenExecutingCommandWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> someExceptionSupplier,
      @Mock MachineShellClientCommand<?> anyCommand) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            someExceptionSupplier));
    when(someExceptionSupplier.get()).thenReturn(Optional.empty());

    Throwable e = catchThrowable(() -> connection.execute(anyCommand));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasNoCause();
  }

  @Test
  void failWhenExecutingCommandWithExceptionWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> someExceptionSupplier,
      @Mock MachineShellClientCommand<?> anyCommand) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            someExceptionSupplier));
    when(someExceptionSupplier.get()).thenReturn(Optional.of(SOME_CLIENT_EXCEPTION));

    Throwable e = catchThrowable(() -> connection.execute(anyCommand));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void failWhenAccessingFileWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> someExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            someExceptionSupplier));
    when(someExceptionSupplier.get()).thenReturn(Optional.empty());

    Throwable e = catchThrowable(() -> connection.file(ANY_FILE_PATH));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasNoCause();
  }

  @Test
  void failWhenAccessingFileWithExceptionWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> someExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            someExceptionSupplier));
    when(someExceptionSupplier.get()).thenReturn(Optional.of(SOME_CLIENT_EXCEPTION));

    Throwable e = catchThrowable(() -> connection.file(ANY_FILE_PATH));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void cannotCloseNowWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            anyExceptionSupplier));

    Throwable e = catchThrowable(() -> connection.closeNow());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotReleaseWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            anyExceptionSupplier));

    Throwable e = catchThrowable(() -> connection.release());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void logWarningOnConnectionClosedWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            anyExceptionSupplier));

    connection.connectionClosed(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(equalTo("Connection was already closed"))));
  }

  @Test
  void logWarningOnConnectionClosedWithExceptionWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            anyExceptionSupplier));

    connection.connectionClosed(new MachineShellClientConnectionEvent(
        someClientConnection,
        ANY_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(equalTo("Connection was already closed"))));
  }

  @Test
  void logWarningOnConnectionErrorOccurredWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            anyExceptionSupplier));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(equalTo("Connection closed"))));
  }

  @Test
  void logWarningOnConnectionErrorOccurredWithExceptionWhenClosed(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosedState(
            anyStateChanger,
            anyExceptionSupplier));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(
        someClientConnection,
        ANY_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(equalTo("Connection closed"))));
  }

  @Test
  void requestNotAvailableWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(anyStateChanger));

    boolean available = connection.request(ANY_USERNAME);

    assertThat(available).isFalse();
  }

  @Test
  void cannotAcquireWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(anyStateChanger));

    Throwable e = catchThrowable(() -> connection.acquire());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void failWhenExecutingCommandWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientCommand<?> anyCommand) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(anyStateChanger));

    Throwable e = catchThrowable(() -> connection.execute(anyCommand));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void failWhenAccessingFileWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(anyStateChanger));

    Throwable e = catchThrowable(() -> connection.file(ANY_FILE_PATH));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void logFineWhenTryingToCloseNowWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(anyStateChanger));

    connection.closeNow();

    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(FINE))
        .withMessage(equalTo("Connection already closing"))));
  }

  @Test
  void cannotReleaseWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(anyStateChanger));

    Throwable e = catchThrowable(() -> connection.release());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void onConnectionClosedWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(someStateChanger));

    connection.connectionClosed(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).closed(
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent()));
  }

  @Test
  void onConnectionClosedWithExceptionWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(someStateChanger));

    connection.connectionClosed(new MachineShellClientConnectionEvent(
        someClientConnection,
        SOME_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).closed(argThat(exceptionSupplier -> exceptionSupplier.get()
        .map(SOME_CLIENT_EXCEPTION::equals)
        .orElse(false)));
  }

  @Test
  void onErrorOccurredWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(someStateChanger));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).errorOccurred(
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent()));
  }

  @Test
  void onErrorOccurredWithExceptionWhenClosing(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionClosingState(someStateChanger));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(
        someClientConnection,
        SOME_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).errorOccurred(argThat(exceptionSupplier -> exceptionSupplier.get()
        .map(SOME_CLIENT_EXCEPTION::equals)
        .orElse(false)));
  }

  @Test
  void cannotRequestWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            anyExceptionSupplier));

    Throwable e = catchThrowable(() -> connection.request(ANY_USERNAME));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotAcquireWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            anyExceptionSupplier));

    Throwable e = catchThrowable(() -> connection.acquire());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void failWhenExecutingCommandWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> someExceptionSupplier,
      @Mock MachineShellClientCommand<?> anyCommand) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            someExceptionSupplier));
    when(someExceptionSupplier.get()).thenReturn(Optional.empty());

    Throwable e = catchThrowable(() -> connection.execute(anyCommand));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasNoCause();
  }

  @Test
  void failWhenExecutingCommandWithExceptionWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> someExceptionSupplier,
      @Mock MachineShellClientCommand<?> anyCommand) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            someExceptionSupplier));
    when(someExceptionSupplier.get()).thenReturn(Optional.of(SOME_CLIENT_EXCEPTION));

    Throwable e = catchThrowable(() -> connection.execute(anyCommand));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void failWhenAccessingFileWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> someExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            someExceptionSupplier));
    when(someExceptionSupplier.get()).thenReturn(Optional.empty());

    Throwable e = catchThrowable(() -> connection.file(ANY_FILE_PATH));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasNoCause();
  }

  @Test
  void failWhenAccessingFileWithExceptionWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> someExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            someExceptionSupplier));
    when(someExceptionSupplier.get()).thenReturn(Optional.of(SOME_CLIENT_EXCEPTION));

    Throwable e = catchThrowable(() -> connection.file(ANY_FILE_PATH));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void cannotCloseNowWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            anyExceptionSupplier));

    Throwable e = catchThrowable(() -> connection.closeNow());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotReleaseWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            anyExceptionSupplier));

    Throwable e = catchThrowable(() -> connection.release());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void logWarningOnConnectionClosedWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            anyExceptionSupplier));

    connection.connectionClosed(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(equalTo("Error occurred on connection"))));
  }

  @Test
  void logWarningOnConnectionClosedWithExceptionWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            anyExceptionSupplier));

    connection.connectionClosed(new MachineShellClientConnectionEvent(
        someClientConnection,
        ANY_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(equalTo("Error occurred on connection"))));
  }

  @Test
  void logWarningOnErrorOccurredWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            anyExceptionSupplier));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(equalTo("Error was already occurred on connection"))));
  }

  @Test
  void logWarningOnErrorOccurredWithExceptionWhenErrorOccurred(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock Supplier<Optional<Exception>> anyExceptionSupplier,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionErrorOccurredState(
            anyStateChanger,
            anyExceptionSupplier));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(
        someClientConnection,
        ANY_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(loggerHandler).publish(argThat(someLogRecord()
        .withLevel(equalTo(WARNING))
        .withMessage(equalTo("Error was already occurred on connection"))));
  }

  @Test
  void requestNotAvailableWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            anyStateChanger,
            anyBridge,
            ANY_USERNAME));

    boolean available = connection.request(ANY_USERNAME);

    assertThat(available).isFalse();
  }

  @Test
  void acquireWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge someBridge) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            someStateChanger,
            someBridge,
            SOME_USERNAME));

    connection.acquire();

    verify(someStateChanger).acquired(someBridge, SOME_USERNAME);
  }

  @Test
  void failWhenExecutingCommandWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientCommand<?> anyCommand) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            anyStateChanger,
            anyBridge,
            ANY_USERNAME));

    Throwable e = catchThrowable(() -> connection.execute(anyCommand));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void failWhenAccessingFileWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            anyStateChanger,
            anyBridge,
            ANY_USERNAME));

    Throwable e = catchThrowable(() -> connection.file(ANY_FILE_PATH));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void closeNowThroughBridgeWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge someBridge) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            someStateChanger,
            someBridge,
            ANY_USERNAME));

    connection.closeNow();

    InOrder order = inOrder(someStateChanger, someBridge);
    order.verify(someStateChanger).closing();
    order.verify(someBridge).closeNow();
  }

  @Test
  void cannotReleaseWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger anyStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            anyStateChanger,
            anyBridge,
            ANY_USERNAME));

    Throwable e = catchThrowable(() -> connection.release());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void onConnectionClosedWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME));

    connection.connectionClosed(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).closed(
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent()));
  }

  @Test
  void onConnectionClosedWithExceptionWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME));

    connection.connectionClosed(new MachineShellClientConnectionEvent(
        someClientConnection,
        SOME_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).closed(argThat(exceptionSupplier -> exceptionSupplier.get()
        .map(SOME_CLIENT_EXCEPTION::equals)
        .orElse(false)));
  }

  @Test
  void onErrorOccurredWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(someClientConnection));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).errorOccurred(
        argThat(exceptionSupplier -> !exceptionSupplier.get().isPresent()));
  }

  @Test
  void onErrorOccurredWithExceptionWhenRequested(
      @Mock PooledMachineShellClientConnectionStateChanger someStateChanger,
      @Mock MachineShellClientConnectionBridge anyBridge,
      @Mock MachineShellClientConnection someClientConnection) {
    PooledMachineShellClientConnection connection = new PooledMachineShellClientConnection(
        removeAction,
        connectionConstructor,
        changeStateAction -> new PooledMachineShellClientConnectionRequestedState(
            someStateChanger,
            anyBridge,
            ANY_USERNAME));

    connection.connectionErrorOccurred(new MachineShellClientConnectionEvent(
        someClientConnection,
        SOME_CLIENT_EXCEPTION));

    verify(removeAction).accept(connection);
    verify(someClientConnection).removeConnectionEventListener(connection);
    verify(someStateChanger).errorOccurred(argThat(exceptionSupplier -> exceptionSupplier.get()
        .map(SOME_CLIENT_EXCEPTION::equals)
        .orElse(false)));
  }
}
