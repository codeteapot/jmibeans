package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnection;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientConnectionFactory;

@ExtendWith(MockitoExtension.class)
class PoolingMachineShellConnectionFactoryAcceptanceTest {

  private static final Duration SOME_IDLE_TIMEOUT = ofMillis(200L);
  private static final long SOME_CLOSE_WAIT_MILLIS = 240L;

  private static final String SOME_USERNAME = "scott";

  private static final String SOME_COMMAND_STATEMENT = "some --statement";

  private static final Object FIRST_EXECUTION_RESULT = new Object();
  private static final Object SECOND_EXECUTION_RESULT = new Object();

  @Mock
  private MachineShellClientConnectionFactory clientConnectionFactory;

  private PoolingMachineShellConnectionFactory connectionFactory;

  @BeforeEach
  void setUp() {
    connectionFactory = new PoolingMachineShellConnectionFactory(clientConnectionFactory);
  }

  @Test
  void reuseManagedConnection(
      @Mock MachineShellClientConnection firstManagedConnection,
      @Mock MachineShellClientConnection secondManagedConnection,
      @Mock MachineShellCommand<Object> someCommand) throws Exception {
    when(clientConnectionFactory.getConnection(SOME_USERNAME))
        .thenReturn(firstManagedConnection, secondManagedConnection);
    when(firstManagedConnection.execute(argThat(
        command -> command.getStatement().equals(SOME_COMMAND_STATEMENT))))
            .thenReturn(FIRST_EXECUTION_RESULT);
    when(secondManagedConnection.execute(argThat(
        command -> command.getStatement().equals(SOME_COMMAND_STATEMENT))))
            .thenReturn(SECOND_EXECUTION_RESULT);
    when(someCommand.getStatement())
        .thenReturn(SOME_COMMAND_STATEMENT);

    connectionFactory.setIdleTimeout(SOME_IDLE_TIMEOUT);
    try (MachineShellConnection connection = connectionFactory.getConnection(SOME_USERNAME)) {
      Object result = connection.execute(someCommand);
      assertThat(result).isEqualTo(FIRST_EXECUTION_RESULT);
    }
    try (MachineShellConnection connection = connectionFactory.getConnection(SOME_USERNAME)) {
      Object result = connection.execute(someCommand);
      assertThat(result).isEqualTo(FIRST_EXECUTION_RESULT);
    }
    sleep(SOME_CLOSE_WAIT_MILLIS);
    try (MachineShellConnection connection = connectionFactory.getConnection(SOME_USERNAME)) {
      Object result = connection.execute(someCommand);
      assertThat(result).isEqualTo(SECOND_EXECUTION_RESULT);
    }
  }
}
