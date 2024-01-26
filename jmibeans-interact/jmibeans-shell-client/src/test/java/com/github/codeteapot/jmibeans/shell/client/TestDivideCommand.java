package com.github.codeteapot.jmibeans.shell.client;

import java.nio.charset.Charset;

class TestDivideCommand implements MachineShellClientCommand<Integer> {

  private final int dividend;
  private final int divisor;
  private final long delayMillis;
  private final Runnable beforeExecution;
  private final boolean prematureOutputClosing;

  TestDivideCommand(int dividend, int divisor) {
    this(dividend, divisor, 0L, TestDivideCommand::doNothing, false);
  }

  TestDivideCommand(int dividend, int divisor, long delayMillis) {
    this(dividend, divisor, delayMillis, TestDivideCommand::doNothing, false);
  }

  TestDivideCommand(int dividend, int divisor, Runnable beforeExecution) {
    this(dividend, divisor, 0L, beforeExecution, false);
  }

  TestDivideCommand(int dividend, int divisor, boolean prematureOutputClosing) {
    this(dividend, divisor, 0L, TestDivideCommand::doNothing, prematureOutputClosing);
  }

  private TestDivideCommand(
      int dividend,
      int divisor,
      long delayMillis,
      Runnable beforeExecution,
      boolean prematureOutputClosing) {
    this.dividend = dividend;
    this.divisor = divisor;
    this.delayMillis = delayMillis;
    this.beforeExecution = beforeExecution;
    this.prematureOutputClosing = prematureOutputClosing;
  }

  @Override
  public String getStatement() {
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
  public MachineShellClientCommandExecution<Integer> getExecution(Charset charset) {
    beforeExecution.run();
    return new TestDivideCommandExecution(charset, dividend, divisor, prematureOutputClosing);
  }

  private static void doNothing() {}
}
