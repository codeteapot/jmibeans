package com.github.codeteapot.jmibeans.session;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import com.github.codeteapot.jmibeans.session.SSHMachineSessionFileDetached;
import com.github.codeteapot.testing.logging.LoggerStub;
import java.util.logging.Handler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SSHMachineSessionFileDetachedTest {

  private LoggerStub loggerStub;

  @Mock
  private Handler loggerHandler;

  @BeforeEach
  public void setUp() {
    loggerStub = loggerStubFor(SSHMachineSessionFileDetached.class.getName(), loggerHandler);
  }

  @AfterEach
  public void tearDown() {
    loggerStub.restore();
  }

  @Test
  public void detachAlreadyDetachedFile() {
    SSHMachineSessionFileDetached file = new SSHMachineSessionFileDetached();

    boolean detached = file.detach();

    assertThat(detached).isTrue();
    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)));
  }
}
