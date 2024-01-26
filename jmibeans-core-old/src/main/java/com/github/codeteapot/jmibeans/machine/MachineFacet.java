package com.github.codeteapot.jmibeans.machine;

import com.github.codeteapot.jmibeans.Machine;
import com.github.codeteapot.jmibeans.MachineFacetFactory;

/**
 * Subset of operations exposed by a {@link Machine}.
 *
 * <p>The corresponding {@link MachineContext} is available at facet creation time.
 *
 * @see MachineFacetFactory#getFacet(MachineContext)
 */
public interface MachineFacet {

  /**
   * The reference of the machine to which the facet is associated.
   *
   * <p>The return value is expected to be obtained by calling {@link MachineContext#getRef()}.
   *
   * @return The reference of the machine to which it is associated.
   */
  MachineRef getRef();
}
