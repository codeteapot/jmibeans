package com.github.codeteapot.jmibeans.testing.shell.statement;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MachineTerminalStatementMatchers {

  private MachineTerminalStatementMatchers() {}
  
  public static Predicate<String> matching(Pattern pattern, PatternMatcherFind... finds) {
    return new PatternMatcher(pattern, finds);
  }
}
