package com.github.codeteapot.jmibeans.session;

import com.github.codeteapot.jmibeans.session.MachineCommand;
import com.github.codeteapot.jmibeans.session.MachineCommandExecution;
import java.nio.charset.Charset;

class TestDivideCommand implements MachineCommand<Integer> {

  private final int dividend;
  private final int divisor;
  private final long delayMillis;
  private final Runnable beforeExecution;

  TestDivideCommand(int dividend, int divisor) {
    this(dividend, divisor, 0L, TestDivideCommand::doNothing);
  }

  TestDivideCommand(int dividend, int divisor, long delayMillis) {
    this(dividend, divisor, delayMillis, TestDivideCommand::doNothing);
  }

  TestDivideCommand(int dividend, int divisor, Runnable beforeExecution) {
    this(dividend, divisor, 0L, beforeExecution);
  }

  TestDivideCommand(int dividend, int divisor, long delayMillis, Runnable beforeExecution) {
    this.dividend = dividend;
    this.divisor = divisor;
    this.delayMillis = delayMillis;
    this.beforeExecution = beforeExecution;
  }

  @Override
  public String getSentence() {
    return new StringBuilder()
        .append("read -p \"dividend:\" dividend\n")
        .append("read -p \"divisor\" divisor\n")
        .append("if [[ $divisor = 0 ]]\n")
        .append("then\n")
        .append("  >&2 echo -n 'division by zero'\n")
        .append("  exit 1\n")
        .append("fi\n")
        .append("sleep ").append((float) delayMillis / 1000F).append("\n")
        .append(">&1 echo -n \"$((dividend / divisor))\"\n")
        .append("exit 0")
        .toString();
  }

  @Override
  public MachineCommandExecution<Integer> getExecution(Charset charset) {
    beforeExecution.run();
    return new TestDivideCommandExecution(charset, dividend, divisor);
  }

  private static void doNothing() {}
}
