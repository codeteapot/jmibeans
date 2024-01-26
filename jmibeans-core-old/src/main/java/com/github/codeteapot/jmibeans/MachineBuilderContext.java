package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.UnknownUserException;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineSessionHostResolutionException;
import com.github.codeteapot.jmibeans.session.MachineSession;

/**
 * Context in which a machine is created.
 *
 * <p>It allows establishing sessions in the created machine and registering facets to it.
 *
 * @see MachineBuilder#build(MachineBuilderContext)
 */
public interface MachineBuilderContext {

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

  /**
   * Registers a facet through its factory.
   *
   * @param factory The factory that will be responsible for instantiating the facet to register.
   *
   * @throws MachineFacetInstantiationException In case the factory fails when instantiating the
   *         facet.
   */
  void register(MachineFacetFactory factory) throws MachineFacetInstantiationException;
}
