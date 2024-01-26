package com.github.codeteapot.testing.hamcrest;

import static java.util.Objects.requireNonNull;

import java.beans.PropertyChangeEvent;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class SomePropertyChangeEventMatcher extends TypeSafeMatcher<PropertyChangeEvent> {

  private Matcher<String> propertyName;
  private Matcher<?> oldValue;
  private Matcher<?> newValue;

  private SomePropertyChangeEventMatcher() {
    propertyName = null;
    oldValue = null;
    newValue = null;
  }

  public SomePropertyChangeEventMatcher withPropertyName(Matcher<String> propertyName) {
    this.propertyName = requireNonNull(propertyName);
    return this;
  }

  public SomePropertyChangeEventMatcher withOldValue(Matcher<?> oldValue) {
    this.oldValue = requireNonNull(oldValue);
    return this;
  }

  public SomePropertyChangeEventMatcher withNewValue(Matcher<?> newValue) {
    this.newValue = requireNonNull(newValue);
    return this;
  }

  @Override
  public void describeTo(Description description) {
    // TODO Matcher description
  }

  public static SomePropertyChangeEventMatcher somePropertyChangeEvent() {
    return new SomePropertyChangeEventMatcher();
  }

  @Override
  protected boolean matchesSafely(PropertyChangeEvent item) {
    if (propertyName != null && !propertyName.matches(item.getPropertyName())) {
      return false;
    }
    if (oldValue != null && !oldValue.matches(item.getOldValue())) {
      return false;
    }
    if (newValue != null && !newValue.matches(item.getNewValue())) {
      return false;
    }
    return true;
  }
}
