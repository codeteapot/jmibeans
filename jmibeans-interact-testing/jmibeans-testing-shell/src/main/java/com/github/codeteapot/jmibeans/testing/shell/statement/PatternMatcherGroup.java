package com.github.codeteapot.jmibeans.testing.shell.statement;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public class PatternMatcherGroup {

  private final Function<Matcher, String> mapper;
  private final Predicate<String> groupMatcher;

  private PatternMatcherGroup(Function<Matcher, String> mapper, Predicate<String> groupMatcher) {
    this.mapper = requireNonNull(mapper);
    this.groupMatcher = requireNonNull(groupMatcher);
  }

  public static PatternMatcherGroup patternGroup(Predicate<String> groupMatcher) {
    return new PatternMatcherGroup(Matcher::group, groupMatcher);
  }

  public static PatternMatcherGroup patternGroup(int group, Predicate<String> groupMatcher) {
    return new PatternMatcherGroup(matcher -> matcher.group(group), groupMatcher);
  }

  public static PatternMatcherGroup patternGroup(String name, Predicate<String> groupMatcher) {
    return new PatternMatcherGroup(matcher -> matcher.group(name), groupMatcher);
  }

  boolean test(Matcher matcher) {
    return groupMatcher.test(mapper.apply(matcher));
  }
}
