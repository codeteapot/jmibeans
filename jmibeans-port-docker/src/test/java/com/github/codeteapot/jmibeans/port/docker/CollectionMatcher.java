package com.github.codeteapot.jmibeans.port.docker;

import java.util.Collection;
import java.util.Vector;

import org.mockito.ArgumentMatcher;

class CollectionMatcher<E> implements ArgumentMatcher<Collection<E>> {

  private Integer size;
  private Collection<ArgumentMatcher<E>> elements;

  CollectionMatcher() {
    size = null;
    elements = new Vector<>();
  }

  CollectionMatcher<E> withSize(int size) {
    this.size = size;
    return this;
  }

  CollectionMatcher<E> withElement(ArgumentMatcher<E> element) {
    elements.add(element);
    return this;
  }

  @Override
  public boolean matches(Collection<E> argument) {
    if (size != null && !size.equals(argument.size())) {
      return false;
    }
    for (ArgumentMatcher<E> element : elements) {
      if (argument.stream().noneMatch(element::matches)) {
        return false;
      }
    }
    return true;
  }

}
