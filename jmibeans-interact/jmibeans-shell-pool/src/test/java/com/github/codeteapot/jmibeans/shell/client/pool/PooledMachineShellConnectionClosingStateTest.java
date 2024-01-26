package com.github.codeteapot.jmibeans.shell.client.pool;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.util.logging.Level.FINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

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
class PooledMachineShellConnectionClosingStateTest {

  private static final String ANY_USERNAME = "any";
  private static final String ANY_FILE_PATH = "/any/file";

  @Mock
  private Handler someLoggerHandler;

  @Mock
  private PooledMachineShellClientConnectionStateChanger stateChanger;

  private LoggerStub loggerStub;

  private PooledMachineShellClientConnectionClosingState state;

  @BeforeEach
  void setUp() {
    loggerStub = loggerStubFor(
        PooledMachineShellClientConnectionClosingState.class.getName(),
        someLoggerHandler);
    state = new PooledMachineShellClientConnectionClosingState(stateChanger);
  }

  @AfterEach
  void tearDown() {
    loggerStub.restore();
  }

  @Test
  void requestNotAvailable() {
    boolean available = state.request(ANY_USERNAME);

    assertThat(available).isFalse();
  }

  @Test
  void cannotAcquire() {
    Throwable e = catchThrowable(() -> state.acquire());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void failWhenExecutingCommand(@Mock MachineShellClientCommand<?> anyCommand) {
    Throwable e = catchThrowable(() -> state.execute(anyCommand));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void failWhenAccessingFile() {
    Throwable e = catchThrowable(() -> state.file(ANY_FILE_PATH));

    assertThat(e).isInstanceOf(MachineShellClientException.class);
  }

  @Test
  void logFineWhenTryingToCloseNow() {
    state.closeNow();

    verify(someLoggerHandler).publish(argThat(record -> record.getLevel().equals(FINE)
        && "Connection already closing".equals(record.getMessage())));
  }

  @Test
  void cannotRelease() {
    Throwable e = catchThrowable(() -> state.release());

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }
}
