package com.github.codeteapot.jmibeans.testing.shell.statement;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.regex.Matcher;

public class PatternMatcherFind {

  private final Predicate<Matcher> mapper;
  private final PatternMatcherGroup[] groups;

  private PatternMatcherFind(Predicate<Matcher> mapper, PatternMatcherGroup[] groups) {
    this.mapper = requireNonNull(mapper);
    this.groups = requireNonNull(groups);
  }

  public static PatternMatcherFind patternFind(PatternMatcherGroup... groups) {
    return new PatternMatcherFind(Matcher::find, groups);
  }

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
