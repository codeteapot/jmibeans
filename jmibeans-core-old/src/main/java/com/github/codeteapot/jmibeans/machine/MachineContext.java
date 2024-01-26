package com.github.codeteapot.jmibeans.machine;

import com.github.codeteapot.jmibeans.Machine;
import com.github.codeteapot.jmibeans.MachineFacetFactory;
import com.github.codeteapot.jmibeans.MachineRealm;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineSessionHostResolutionException;
import com.github.codeteapot.jmibeans.session.MachineSession;

/**
 * Particular context of a {@link Machine} instance.
 *
 * <p>All facets of a machine share the same context.
 * 
 * <p>The context is created when a machine is noticed by the platform and remains in effect as long
 * as the machine is available.
 *
 * @see MachineFacetFactory#getFacet(MachineContext)
 */
public interface MachineContext {

  /**
   * The reference of the machine associated to this context.
   *
   * <p>Implementations of {@link MachineFacet#getRef()} are expected to make use of this method to
   * get the return value.
   * <pre>
   * class SomeMachineFacet implements MachineFacet {
   *
   *   private final MachineContext context;
   *
   *   SomeMachineFacet(MachineContext context) {
   *     this.context = requireNonNull(context);
   *   }
   *
   *  {@literal @Override}
   *   public MachineRef getRef() {
   *     return context.getRef();
   *   }
   * }
   * </pre>
   *
   * @return The associated machine reference.
   */
  MachineRef getRef();

  /**
   * Establishes a session to a machine for the specified user.
   *
   * @param username The user name for which the session is established.
   *
   * @return The established session.
   *
   * @throws UnknownUserException In case the user is not recognized in the realm of the machine
   *         profile.
   * @throws MachineSessionHostResolutionException In case of not being able to resolve the session
   *         host of the machine.
   *
   * @see MachineRealm#authentication(String)
   * @see MachineLink#getSessionHost(com.github.codeteapot.jmibeans.port.MachineNetworkName)
   */
  MachineSession getSession(String username)
      throws UnknownUserException, MachineSessionHostResolutionException;
}
