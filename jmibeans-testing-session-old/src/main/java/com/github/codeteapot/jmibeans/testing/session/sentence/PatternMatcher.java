package com.github.codeteapot.jmibeans.testing.session.sentence;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PatternMatcher implements Predicate<String> {

  private final Pattern pattern;
  private final PatternMatcherFind[] finds;

  PatternMatcher(Pattern pattern, PatternMatcherFind[] finds) {
    this.pattern = requireNonNull(pattern);
    this.finds = requireNonNull(finds);
  }

  @Override
  public boolean test(String t) {
    Matcher matcher = pattern.matcher(t);
    for (PatternMatcherFind find : finds) {
      if (!find.test(matcher)) {
        return false;
      }
    }
    return true;
  }
}
