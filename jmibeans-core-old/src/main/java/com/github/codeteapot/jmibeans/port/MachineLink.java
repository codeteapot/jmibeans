package com.github.codeteapot.jmibeans.port;

import java.net.InetAddress;

/**
 * Link to infrastructure for a machine accepted by the platform.
 *
 * <p>Available at machine spawn time, through the
 * {@link MachineManager#accept(MachineId, MachineLink)} method.
 *
 * <p>Each implementation of {@link PlatformPort} decides what the behavior of each of the methods
 * of the link is.
 */
public interface MachineLink {

  /**
   * The name of the profile in the available catalog that will be used for the machine.
   *
   * @return The name of the profile for the machine.
   */
  MachineProfileName getProfileName();

  /**
   * Gets the address used to establish sessions on the machine on the specified network.
   *
   * <p>If the network with the specified name is not recognized by the port, or if for some reason
   * the address on that network cannot be determined, the call to this method throws a
   * {@link MachineSessionHostResolutionException}.
   *
   * @param networkName Name of the network in which the address of the machine resolves.
   *
   * @return The address at which machine sessions should be established.
   *
   * @throws MachineSessionHostResolutionException In case of not recognizing the network or not
   *         being able to determine the address in it.
   */
  InetAddress getSessionHost(MachineNetworkName networkName)
      throws MachineSessionHostResolutionException;
}
