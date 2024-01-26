package com.github.codeteapot.jmibeans.shell.client.pool;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.testing.logging.LoggerStub;

@ExtendWith(MockitoExtension.class)
class PooledMachineShellConnectionClosedStateTest {

  private static final String ANY_USERNAME = "any";
  private static final String ANY_FILE_PATH = "/any/file";

  private static final MachineShellClientException ANY_CLIENT_EXCEPTION =
      new MachineShellClientException(new Exception());;

  private static final MachineShellClientException SOME_CLIENT_EXCEPTION =
      new MachineShellClientException(new Exception());

  @Mock
  private Handler someLoggerHandler;

  @Mock
  private PooledMachineShellClientConnectionStateChanger stateChanger;

  @Mock
  private Supplier<Optional<Exception>> exceptionSupplier;

  private LoggerStub loggerStub;

  private PooledMachineShellClientConnectionClosedState state;

  @BeforeEach
  void setUp() {
    loggerStub = loggerStubFor(
        PooledMachineShellClientConnectionClosedState.class.getName(),
        someLoggerHandler);
    state = new PooledMachineShellClientConnectionClosedState(stateChanger, exceptionSupplier);
  }

  @AfterEach
  void tearDown() {
    loggerStub.restore();
  }

  @Test
  void cannotRequest() {
    Throwable e = catchThrowable(() -> state.request(ANY_USERNAME));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotAcquire() {
    Throwable e = catchThrowable(() -> state.acquire());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void failWhenExecutingCommand(@Mock MachineShellClientCommand<?> anyCommand) {
    when(exceptionSupplier.get()).thenReturn(Optional.empty());

    Throwable e = catchThrowable(() -> state.execute(anyCommand));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasNoCause();
  }

  @Test
  void failWhenExecutingCommandWithException(@Mock MachineShellClientCommand<?> anyCommand) {
    when(exceptionSupplier.get()).thenReturn(Optional.of(SOME_CLIENT_EXCEPTION));

    Throwable e = catchThrowable(() -> state.execute(anyCommand));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void failWhenAccessingFile() {
    when(exceptionSupplier.get()).thenReturn(Optional.empty());

    Throwable e = catchThrowable(() -> state.file(ANY_FILE_PATH));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasNoCause();
  }

  @Test
  void failWhenAccessingFileWithException() {
    when(exceptionSupplier.get()).thenReturn(Optional.of(SOME_CLIENT_EXCEPTION));

    Throwable e = catchThrowable(() -> state.file(ANY_FILE_PATH));

    assertThat(e)
        .isInstanceOf(MachineShellClientException.class)
        .hasCause(SOME_CLIENT_EXCEPTION);
  }

  @Test
  void cannotCloseNow() {
    Throwable e = catchThrowable(() -> state.closeNow());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotRelease() {
    Throwable e = catchThrowable(() -> state.release());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void logWarningOnClosed() {
    state.onClosed();

    verify(someLoggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)
        && "Connection was already closed".equals(record.getMessage())));
  }

  @Test
  void logWarningOnClosedWithException() {
    state.onClosed(ANY_CLIENT_EXCEPTION);

    verify(someLoggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)
        && "Connection was already closed".equals(record.getMessage())));
  }

  @Test
  void logWarningOnErrorOccurred() {
    state.onErrorOccurred();

    verify(someLoggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)
        && "Connection closed".equals(record.getMessage())));
  }

  @Test
  void logWarningOnErrorOccurredWithException() {
    state.onErrorOccurred(ANY_CLIENT_EXCEPTION);

    verify(someLoggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)
        && "Connection closed".equals(record.getMessage())));
  }
}
