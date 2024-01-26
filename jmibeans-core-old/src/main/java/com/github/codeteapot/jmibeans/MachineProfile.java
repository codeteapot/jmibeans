package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineNetworkName;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import java.util.Optional;

/**
 * Characteristics of a set of machines from the catalog.
 *
 * <p>For every available machine there corresponds one, and only one profile.
 *
 * @see MachineCatalog#getProfile(com.github.codeteapot.jmibeans.port.MachineProfileName)
 */
public interface MachineProfile {

  /**
   * Properties of the session pool that is created to host the sessions established on the machine.
   *
   * @return The properties of the session pool.
   */
  MachineSessionPool getSessionPool();

  /**
   * Authentication methods for recognized users of the machine.
   *
   * @return The realm with the authentication methods for recognized users.
   */
  MachineRealm getRealm();

  /**
   * Name of the network through which the sessions are established on the machine.
   *
   * <p>This network name is used to get the session host via the {@link MachineLink} that is
   * available when the machine is created.
   *
   * @return The name of the network through which the sessions are established.
   * 
   * @see MachineLink#getSessionHost(MachineNetworkName)
   */
  MachineNetworkName getNetworkName();

  /**
   * Port used to establish machine sessions.
   *
   * <p>If empty, the default port of the session factory is used.
   *
   * @return The port used to establish the sessions.
   * 
   * @see MachineSessionFactory#getSession(
   *        java.net.InetAddress,
   *        Integer,
   *        String,
   *        com.github.codeteapot.jmibeans.session.MachineSessionAuthentication)
   */
  Optional<Integer> getSessionPort();

  /**
   * The builder responsible for registering the facets to the machine.
   *
   * @return The builder instance.
   */
  MachineBuilder getBuilder();
}
