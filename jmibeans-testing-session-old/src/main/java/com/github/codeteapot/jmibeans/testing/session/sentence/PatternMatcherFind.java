package com.github.codeteapot.jmibeans.testing.session.sentence;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.regex.Matcher;

/**
 * Find operation of a regular expression matcher.
 *
 * <p>Matches if the find operation matches, and so do any group operations it has.
 *
 * @see MachineTerminalSentenceMatchers#matching(java.util.regex.Pattern, PatternMatcherFind...)
 */
public class PatternMatcherFind {

  private final Predicate<Matcher> mapper;
  private final PatternMatcherGroup[] groups;

  private PatternMatcherFind(Predicate<Matcher> mapper, PatternMatcherGroup[] groups) {
    this.mapper = requireNonNull(mapper);
    this.groups = requireNonNull(groups);
  }

  /**
   * Find operation equivalent to {@link Matcher#find()}.
   *
   * @param groups Group operations on the current match.
   *
   * @return The find operation.
   */
  public static PatternMatcherFind patternFind(PatternMatcherGroup... groups) {
    return new PatternMatcherFind(Matcher::find, groups);
  }

  /**
   * Find operation equivalent to {@link Matcher#find(int)}.
   *
   * @param start Start parameter of the find operation.
   * @param groups Group operations on the current match.
   *
   * @return The find operation.
   */
  public static PatternMatcherFind patternFind(int start, PatternMatcherGroup... groups) {
    return new PatternMatcherFind(matcher -> matcher.find(start), groups);
  }

  boolean test(Matcher matcher) {
    if (!mapper.test(matcher)) {
      return false;
    }
    for (PatternMatcherGroup group : groups) {
      if (!group.test(matcher)) {
        return false;
      }
    }
    return true;
  }
}
