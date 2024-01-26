package com.github.codeteapot.jmibeans.testing.session.sentence;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Collect a set of matchers that meets common needs.
 */
public class MachineTerminalSentenceMatchers {

  private MachineTerminalSentenceMatchers() {}

  /**
   * Create a matcher based on a regular expression.
   *
   * <p>The find operations that must be carried out are also specified. No find operation implies
   * that the matcher returns {@code true}.
   *
   * @param pattern Regular expression to match.
   * @param finds Find operations carried out by the matcher.
   *
   * @return The corresponding sentence predicate.
   */
  public static Predicate<String> matching(Pattern pattern, PatternMatcherFind... finds) {
    return new PatternMatcher(pattern, finds);
  }
}
