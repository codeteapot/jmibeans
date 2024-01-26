package com.github.codeteapot.jmibeans.testing.shell;

import static java.lang.String.format;

import java.nio.charset.Charset;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;

class TestDivisionMachineCommand implements MachineShellCommand<Integer> {

  private final int dividend;
  private final int divisor;

  TestDivisionMachineCommand(int dividend, int divisor) {
    this.dividend = dividend;
    this.divisor = divisor;
  }

  @Override
  public String getStatement() {
    return format("divide %d", dividend);
  }

  @Override
  public MachineShellCommandExecution<Integer> getExecution(Charset charset) {
    return new TestDivisionMachineCommandExecution(divisor);
  }

}
