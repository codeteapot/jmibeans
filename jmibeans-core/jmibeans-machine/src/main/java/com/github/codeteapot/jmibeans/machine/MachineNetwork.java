package com.github.codeteapot.jmibeans.machine;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Properties of an infrastructure provider network from the point of view of a machine.
 */
public interface MachineNetwork {

  /**
   * Name with which the network is publicly identified.
   *
   * @return The name of the network.
   */
  MachineNetworkName getName();

  /**
   * The host address of the machine on this network.
   *
   * @return The host address of the machine.
   *
   * @throws UnknownHostException In case the host address of the machine is not recognized.
   */
  InetAddress getAddress() throws UnknownHostException;
}
