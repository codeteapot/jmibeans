package com.github.codeteapot.jmibeans.machine;

/**
 * Gives a {@link MachineFacet} a chance to do whatever is needed before being discarded.
 */
public interface Disposable {

  /**
   * Method called just before the machine associated with the facet implementing this interface is
   * discarded.
   */
  void dispose();
}
