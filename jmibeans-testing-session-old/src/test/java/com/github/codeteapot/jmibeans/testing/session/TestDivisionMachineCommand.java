package com.github.codeteapot.jmibeans.testing.session;

import static java.lang.String.format;

import com.github.codeteapot.jmibeans.session.MachineCommand;
import com.github.codeteapot.jmibeans.session.MachineCommandExecution;
import java.nio.charset.Charset;

class TestDivisionMachineCommand implements MachineCommand<Integer> {

  private final int dividend;
  private final int divisor;

  TestDivisionMachineCommand(int dividend, int divisor) {
    this.dividend = dividend;
    this.divisor = divisor;
  }

  @Override
  public String getSentence() {
    return format("divide %d", dividend);
  }

  @Override
  public MachineCommandExecution<Integer> getExecution(Charset charset) {
    return new TestDivisionMachineCommandExecution(divisor);
  }

}
