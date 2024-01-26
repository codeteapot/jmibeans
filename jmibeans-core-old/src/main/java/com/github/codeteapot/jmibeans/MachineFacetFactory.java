
package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineContext;
import com.github.codeteapot.jmibeans.machine.MachineFacet;

/**
 * Factory used when registering machine facets.
 *
 * @see MachineBuilderContext#register(MachineFacetFactory)
 */
public interface MachineFacetFactory {

  /**
   * Gets a facet for the specified machine context.
   *
   * <p>All facets of the machine will share the same context.
   *
   * <p>The implementation of {@link MachineFacet#getRef()} is expected to be satisfied by calling
   * the {@link MachineContext#getRef()} method.
   *
   * @param context Machine context for the implemented facet.
   *
   * @return The implemented facet.
   *
   * @throws MachineFacetInstantiationException If there is an error when instantiating the facet.
   */
  MachineFacet getFacet(MachineContext context) throws MachineFacetInstantiationException;
}
