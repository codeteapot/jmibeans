package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

import java.beans.PropertyChangeEvent;

import org.mockito.ArgumentMatcher;

class PropertyChangeEventMatcher<T> implements ArgumentMatcher<PropertyChangeEvent> {

  private String propertyName;
  private ArgumentMatcher<T> oldValue;
  private ArgumentMatcher<T> newValue;

  PropertyChangeEventMatcher() {
    propertyName = null;
    oldValue = null;
    newValue = null;
  }

  PropertyChangeEventMatcher<T> withPropertyName(String propertyName) {
    this.propertyName = requireNonNull(propertyName);
    return this;
  }

  PropertyChangeEventMatcher<T> withOldValue(ArgumentMatcher<T> oldValue) {
    this.oldValue = requireNonNull(oldValue);
    return this;
  }

  PropertyChangeEventMatcher<T> withNewValue(ArgumentMatcher<T> newValue) {
    this.newValue = requireNonNull(newValue);
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean matches(PropertyChangeEvent argument) {
    if (propertyName != null && !propertyName.equals(argument.getPropertyName())) {
      return false;
    }
    if (oldValue != null && !oldValue.matches((T) argument.getOldValue())) {
      return false;
    }
    if (newValue != null && !newValue.matches((T) argument.getNewValue())) {
      return false;
    }
    return true;
  }
}
