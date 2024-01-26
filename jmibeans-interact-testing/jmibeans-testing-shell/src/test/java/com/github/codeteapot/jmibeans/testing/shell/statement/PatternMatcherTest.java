package com.github.codeteapot.jmibeans.testing.shell.statement;

import static com.github.codeteapot.jmibeans.testing.shell.statement //
    .MachineTerminalStatementMatchers.matching;
import static com.github.codeteapot.jmibeans.testing.shell.statement.PatternMatcherFind.patternFind;
import static com.github.codeteapot.jmibeans.testing.shell.statement.PatternMatcherGroup //
    .patternGroup;
import static java.util.function.Predicate.isEqual;
import static java.util.regex.Pattern.compile;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatternMatcherTest {

  private static final Pattern GROUPED_PATTERN_REGEX = compile("([a-z]+):(?<numbers>[0-9]+)");

  private static final String MATCHING_VALUE = "xyz:789|abc:123|lmn:456";
  private static final int MATCHING_VALUE_START = 8;
  private static final String MATCHING_GROUP = "abc:123";
  private static final String MATHING_FIRST_GROUP = "abc";
  private static final String MATHING_SECOND_GROUP_NAME = "numbers";
  private static final String MATHING_SECOND_GROUP = "123";

  private static final String UNMATCHING_VALUE = "///:///|///:///";
  private static final String UNMATCHING_VALUE_ON_SOME_GROUP = "xyz:789|abc:456|lmn:456";

  private Predicate<String> matcher;

  @BeforeEach
  void setUp() {
    matcher = matching(
        GROUPED_PATTERN_REGEX,
        patternFind(
            MATCHING_VALUE_START,
            patternGroup(MATHING_SECOND_GROUP_NAME, isEqual(MATHING_SECOND_GROUP)),
            patternGroup(1, isEqual(MATHING_FIRST_GROUP)),
            patternGroup(isEqual(MATCHING_GROUP))),
        patternFind());
  }

  @Test
  void matchesAll() {
    boolean result = matcher.test(MATCHING_VALUE);

    assertThat(result).isTrue();
  }

  @Test
  void notMatchAtAll() {
    boolean result = matcher.test(UNMATCHING_VALUE);

    assertThat(result).isFalse();
  }

  @Test
  void notMatchSomeGroup() {
    boolean result = matcher.test(UNMATCHING_VALUE_ON_SOME_GROUP);

    assertThat(result).isFalse();
  }
}
