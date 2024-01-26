package com.github.codeteapot.jmibeans.platform;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Access to the machines available on the platform.
 */
public interface PlatformContext {

  /**
   * Gets the platform machines available at the time the operation is called.
   *
   * @return The stream of available machines.
   */
  Stream<ReferencedMachine> available();

  /**
   * Gets the platform machine with the specified reference.
   *
   * @param ref The reference of the machine to obtain.
   *
   * @return The machine obtained or {@code empty} in case the machine does not exist or is no
   *         longer available.
   */
  Optional<Machine> lookup(MachineRef ref);
}
