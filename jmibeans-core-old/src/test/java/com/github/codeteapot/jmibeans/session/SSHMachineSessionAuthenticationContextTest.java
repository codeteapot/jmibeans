package com.github.codeteapot.jmibeans.session;

import static com.github.codeteapot.testing.logging.LoggerStub.loggerStubFor;
import static java.util.logging.Level.WARNING;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.session.MachineSessionIdentityName;
import com.github.codeteapot.jmibeans.session.MachineSessionPasswordName;
import com.github.codeteapot.jmibeans.session.SSHMachineSessionAuthenticationContext;
import com.github.codeteapot.jmibeans.session.SSHMachineSessionPasswordMapper;
import com.github.codeteapot.testing.logging.LoggerStub;
import com.jcraft.jsch.Session;
import java.util.Optional;
import java.util.logging.Handler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SSHMachineSessionAuthenticationContextTest {

  private static final MachineSessionIdentityName ANY_IDENTITY_NAME =
      new MachineSessionIdentityName("any-identity");

  private static final MachineSessionPasswordName SOME_PASSWORD_NAME =
      new MachineSessionPasswordName("some-password");
  private static final String SOME_PASSWORD_NAME_VALUE = "some-password";
  private static final byte[] SOME_PASSWORD = {1, 2, 3};


  private LoggerStub loggerStub;

  @Mock
  private Handler loggerHandler;

  @Mock
  private Session jschSession;

  @Mock
  private SSHMachineSessionPasswordMapper passwordMapper;

  private SSHMachineSessionAuthenticationContext context;

  @BeforeEach
  public void setUp() {
    loggerStub = loggerStubFor(SSHMachineSessionAuthenticationContext.class.getName(), loggerHandler);
    context = new SSHMachineSessionAuthenticationContext(jschSession, passwordMapper);
  }

  @AfterEach
  public void tearDown() {
    loggerStub.restore();
  }

  @Test
  public void logIdentityOnlyNotImplemented() {
    context.setIdentityOnly(ANY_IDENTITY_NAME);

    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING)));
  }

  @Test
  public void addExistingPassword() {
    when(passwordMapper.map(SOME_PASSWORD_NAME))
        .thenReturn(Optional.of(SOME_PASSWORD));

    context.addPassword(SOME_PASSWORD_NAME);

    verify(jschSession).setPassword(SOME_PASSWORD);
  }

  @Test
  public void logWarningWhenAddingNonExistingPassword() {
    when(passwordMapper.map(SOME_PASSWORD_NAME))
        .thenReturn(Optional.empty());

    context.addPassword(SOME_PASSWORD_NAME);

    verify(loggerHandler).publish(argThat(record -> record.getLevel().equals(WARNING) &&
        record.getMessage().contains(SOME_PASSWORD_NAME_VALUE)));
  }
}
