package com.github.codeteapot.jmibeans.testing.session;

import static java.lang.Integer.parseInt;
import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.github.codeteapot.jmibeans.session.MachineCommandExecutionException;
import com.github.codeteapot.jmibeans.testing.session.MachineTerminal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MachineTerminalAcceptanceTest {

  private static final String DIVISION_BY_ZERO_ERROR_MESSAGE = "Division by zero";

  private static final int ZERO_DIVISOR = 0;

  private static final String SOME_DIVISION_COMMAND = "divide 13";
  
  private static final int SOME_DIVIDEND = 13;
  private static final int SOME_DIVISOR = 4;
  private static final int SOME_QUOTIENT = 3;

  private MachineTerminal terminal;

  @BeforeEach
  public void setUp() {
    terminal = new MachineTerminal();
    terminal.given(isEqual(SOME_DIVISION_COMMAND)).behave(context -> {
      try (
          PrintWriter outputWriter = new PrintWriter(context.getOutputStream());
          PrintWriter errorWriter = new PrintWriter(context.getErrorStream());
          BufferedReader inputReader = new BufferedReader(new InputStreamReader(
              context.getInputStream()))) {
        int divisor = parseInt(inputReader.readLine());
        if (divisor == 0) {
          errorWriter.print(DIVISION_BY_ZERO_ERROR_MESSAGE);
          return 2;
        }
        outputWriter.println(SOME_DIVIDEND / divisor);
        return 0;
      } catch (NumberFormatException | IOException e) {
        return 1;
      }
    });
  }

  @Test
  public void executeDivideCommandSuccessfully() throws Exception {
    Optional<Integer> quotient = terminal.execute(new TestDivisionMachineCommand(
        SOME_DIVIDEND,
        SOME_DIVISOR)).getValue();

    assertThat(quotient).hasValue(SOME_QUOTIENT);
  }

  @Test
  public void executeDivideCommandWithError() throws Exception {
    Throwable e = catchThrowable(() -> terminal.execute(new TestDivisionMachineCommand(
        SOME_DIVIDEND,
        ZERO_DIVISOR)).getValue());

    assertThat(e)
        .isInstanceOf(MachineCommandExecutionException.class)
        .hasRootCauseMessage(DIVISION_BY_ZERO_ERROR_MESSAGE);
  }
}
