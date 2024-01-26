package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.util.Collections.singleton;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.github.codeteapot.jmibeans.MachineSessionPoolReleaser;
import com.github.codeteapot.jmibeans.ManagedMachine;
import com.github.codeteapot.jmibeans.ManagedMachineImpl;
import com.github.codeteapot.testing.logging.LoggerStub;
import java.util.Optional;
import java.util.logging.Handler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ManagedMachineTest {

  private static final Exception SOME_DISPOSE_EXCEPTION = new RuntimeException();

  private LoggerStub loggerStub;

  @Mock
  private Handler loggerHandler;

  @Mock
  private MachineSessionPoolReleaser sessionPoolReleaser;

  @Mock
  private TestDisposableMachineFacet facet;

  private ManagedMachine machine;

  @BeforeEach
  public void setUp() {
    loggerStub = loggerStubFor(ManagedMachineImpl.class.getName(), loggerHandler);
    machine = new ManagedMachineImpl(sessionPoolReleaser, singleton(facet));
  }

  @AfterEach
  public void tearDown() {
    loggerStub.restore();
  }

  @Test
  public void giveFacetByType() {
    Optional<TestDisposableMachineFacet> result = machine.getFacet(
        TestDisposableMachineFacet.class);

    assertThat(result).hasValue(facet);
  }

  @Test
  public void disposeFacetsSuccessfullyAndReleasePool() {
    machine.dispose();

    verify(facet).dispose();
    verify(sessionPoolReleaser).releaseAll();
  }

  @Test
  public void logWarningWhenFacetDisposeFails() {
    doThrow(SOME_DISPOSE_EXCEPTION)
        .when(facet).dispose();

    machine.dispose();

    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING) &&
        record.getThrown().equals(SOME_DISPOSE_EXCEPTION)));
    verify(sessionPoolReleaser).releaseAll();
  }
}
