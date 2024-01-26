package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.time.Duration.ofMillis;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.PooledMachineSession;
import com.github.codeteapot.jmibeans.session.MachineCommand;
import com.github.codeteapot.jmibeans.session.MachineSession;
import com.github.codeteapot.jmibeans.session.MachineSessionFile;
import com.github.codeteapot.testing.logging.LoggerStub;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Handler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PooledMachineSessionTest {

  private static final ScheduledFuture<Void> NULL_RELEASE_FUTURE = null;
  private static final boolean MAY_INTERRUPT_IF_RUNNING = true;

  private static final String USERNAME = "username";
  private static final Duration IDLE_TIMEOUT = ofMillis(200L);
  private static final long IDLE_TIMEOUT_VALUE = 200L;

  private static final String SOME_FILE_PATH = "some/file/path";
  private static final Object SOME_COMMAND_RESULT = new Object();

  private static final String ANOTHER_USERNAME = "another-username";

  private static final IOException SOME_CLOSE_EXCEPTION = new IOException();

  private LoggerStub loggerStub;

  @Mock
  private Handler loggerHandler;

  @Mock
  private MachineSession managedSession;

  @Mock
  private ScheduledExecutorService releaseExecutor;

  @Mock
  private Consumer<PooledMachineSession> removeAction;

  private AtomicReference<ScheduledFuture<Void>> releaseFuture;

  private PooledMachineSession session;

  @BeforeEach
  public void setUp() {
    loggerStub = loggerStubFor(PooledMachineSession.class.getName(), loggerHandler);
    releaseFuture = new AtomicReference<>();
    session = new PooledMachineSession(
        managedSession,
        releaseExecutor,
        removeAction,
        USERNAME,
        IDLE_TIMEOUT,
        releaseFuture);
  }

  @AfterEach
  public void tearDown() {
    loggerStub.restore();
  }

  @Test
  public void executeCommand(@Mock MachineCommand<Object> someCommand) throws Exception {
    when(managedSession.execute(someCommand))
        .thenReturn(SOME_COMMAND_RESULT);

    Object result = session.execute(someCommand);

    assertThat(result).isEqualTo(SOME_COMMAND_RESULT);
  }

  @Test
  public void giveFileCommand(@Mock MachineSessionFile someFile) throws Exception {
    when(managedSession.file(SOME_FILE_PATH))
        .thenReturn(someFile);

    MachineSessionFile file = session.file(SOME_FILE_PATH);

    assertThat(file).isEqualTo(someFile);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void scheduleReleaseWhenClosing(@Mock ScheduledFuture<Void> someFuture) throws Exception {
    when(releaseExecutor.schedule(
        any(Callable.class),
        eq(IDLE_TIMEOUT_VALUE),
        eq(TimeUnit.MILLISECONDS))).thenReturn(someFuture);

    session.close();

    assertThat(releaseFuture).hasValue(someFuture);
  }

  @Test
  public void availableWhenNotEmptyReleaseFutureAndSameUsername(
      @Mock ScheduledFuture<Void> someFuture) {
    releaseFuture.set(someFuture);

    boolean result = session.available(USERNAME);

    assertThat(result).isTrue();
  }

  @Test
  public void notAvailableWhenEmptyReleaseFutureEvenSameUsername(
      @Mock ScheduledFuture<Void> someFuture) {
    boolean result = session.available(USERNAME);

    assertThat(result).isFalse();
  }

  @Test
  public void notAvailableWhenNotEmptyReleaseFutureButDifferentUsername(
      @Mock ScheduledFuture<Void> someFuture) {
    releaseFuture.set(someFuture);

    boolean result = session.available(ANOTHER_USERNAME);

    assertThat(result).isFalse();
  }

  @Test
  public void cancelAndDropReleaseFutureWhenAcquiring(@Mock ScheduledFuture<Void> someFuture) {
    releaseFuture.set(someFuture);

    session.acquire();

    assertThat(releaseFuture).hasValue(NULL_RELEASE_FUTURE);
    verify(someFuture).cancel(MAY_INTERRUPT_IF_RUNNING);
  }

  @Test
  public void cancelRemoveAndCloseWhenReleasing(
      @Mock ScheduledFuture<Void> someFuture) throws Exception {
    releaseFuture.set(someFuture);

    session.releaseNow();

    InOrder order = inOrder(someFuture, removeAction, managedSession);
    order.verify(someFuture).cancel(MAY_INTERRUPT_IF_RUNNING);
    order.verify(removeAction).accept(session);
    order.verify(managedSession).close();
  }

  @Test
  public void cancelRemoveAndLogWarningWhenReleasingWithCloseError(
      @Mock ScheduledFuture<Void> someFuture) throws Exception {
    releaseFuture.set(someFuture);
    doThrow(SOME_CLOSE_EXCEPTION)
        .when(managedSession).close();

    session.releaseNow();

    InOrder order = inOrder(someFuture, removeAction, loggerHandler);
    order.verify(someFuture).cancel(MAY_INTERRUPT_IF_RUNNING);
    order.verify(removeAction).accept(session);
    order.verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING) &&
        record.getThrown().equals(SOME_CLOSE_EXCEPTION)));
  }
}
