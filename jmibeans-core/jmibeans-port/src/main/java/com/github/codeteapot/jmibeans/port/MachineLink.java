package com.github.codeteapot.jmibeans.port;

import com.github.codeteapot.jmibeans.machine.MachineAgent;

/**
 * Access to the elements necessary for building a machine.
 *
 * <p>
 * It must be implemented by the infrastructure provider.
 *
 * @see MachineManager#accept(byte[], MachineLink)
 */
public interface MachineLink {

  /**
   * Name of the profile assigned to the machine.
   *
   * @return The profile name.
   */
  MachineProfileName getProfileName();

  /**
   * Properties for the machine builder.
   *
   * @return The properties for the builder.
   */
  MachineBuilderProperties getBuilderProperties();

  /**
   * Agent representing the machine.
   *
   * @return The representative agent.
   */
  MachineAgent getAgent();
}
