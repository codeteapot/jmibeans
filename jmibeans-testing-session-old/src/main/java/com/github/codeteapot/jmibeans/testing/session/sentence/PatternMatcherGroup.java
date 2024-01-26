package com.github.codeteapot.jmibeans.testing.session.sentence;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;

/**
 * Group operation on a find operation.
 *
 * <p>Matches if the specified group is found and its value matches the group matcher.
 *
 * @see PatternMatcherFind#patternFind(PatternMatcherGroup...)
 * @see PatternMatcherFind#patternFind(int, PatternMatcherGroup...)
 */
public class PatternMatcherGroup {

  private final Function<Matcher, String> mapper;
  private final Predicate<String> groupMatcher;

  private PatternMatcherGroup(Function<Matcher, String> mapper, Predicate<String> groupMatcher) {
    this.mapper = requireNonNull(mapper);
    this.groupMatcher = requireNonNull(groupMatcher);
  }

  /**
   * Group operation equivalent to {@link Matcher#group()}.
   *
   * @param groupMatcher Matcher applied on group result.
   *
   * @return The group operation.
   */
  public static PatternMatcherGroup patternGroup(Predicate<String> groupMatcher) {
    return new PatternMatcherGroup(Matcher::group, groupMatcher);
  }

  /**
   * Group operation equivalent to {@link Matcher#group(int)}.
   *
   * @param group Group index parameter on group operation.
   * @param groupMatcher Matcher applied on group result.
   *
   * @return The group operation.
   */
  public static PatternMatcherGroup patternGroup(int group, Predicate<String> groupMatcher) {
    return new PatternMatcherGroup(matcher -> matcher.group(group), groupMatcher);
  }

  /**
   * Group operation equivalent to {@link Matcher#group(String)}.
   *
   * @param name Group name parameter on group operation.
   * @param groupMatcher Matcher applied on group result.
   *
   * @return The group operation.
   */
  public static PatternMatcherGroup patternGroup(String name, Predicate<String> groupMatcher) {
    return new PatternMatcherGroup(matcher -> matcher.group(name), groupMatcher);
  }

  boolean test(Matcher matcher) {
    return groupMatcher.test(mapper.apply(matcher));
  }
}
