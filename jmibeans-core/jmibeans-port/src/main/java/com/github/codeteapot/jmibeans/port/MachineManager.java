package com.github.codeteapot.jmibeans.port;

/**
 * Responsible for managing machine availability on the platform.
 *
 * <p>
 * It acts as a bridge between the infrastructure provider and the platform, which is responsible
 * for its implementation.
 *
 * @see PlatformPort#listen(MachineManager)
 */
public interface MachineManager {

  /**
   * Operation called by the port when a machine becomes available.
   *
   * <p>
   * Start the build of a machine, given the {@code machineId}, which is an identifier in the port
   * scope, and the elements necessary for the build through the {@code machineLink}.
   *
   * @param machineId The machine identifier within the port scope.
   * @param machineLink The object with elements necessary for the build of the machine.
   */
  void accept(byte[] machineId, MachineLink machineLink);

  /**
   * Operation called by the port when a machine becomes unavailable.
   *
   * @param machineId The machine identifier within the port scope.
   */
  void forget(byte[] machineId);
}
