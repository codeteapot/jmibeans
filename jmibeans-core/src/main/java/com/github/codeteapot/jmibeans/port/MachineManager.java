package com.github.codeteapot.jmibeans.port;

import com.github.codeteapot.jmibeans.PlatformAdapter;

/**
 * Management of known machines from the point of view of the platform port.
 *
 * <p>The platform adapter is responsible for passing the instance associated with the port in the
 * call to {@link PlatformAdapter#listen(PlatformPort)}.
 *
 * @see PlatformPort#listen(MachineManager)
 */
public interface MachineManager {

  /**
   * Let the platform accept the machine with the specified identifier and for the associated port.
   *
   * @param id The machine identifier in the underlying infrastructure of the port.
   * @param link Link to the underlying infrastructure of the port.
   */
  void accept(MachineId id, MachineLink link);

  /**
   * Let the port forget the machine with the specified identifier, for the associated port.
   *
   * @param id The machine identifier in the underlying infrastructure of the port.
   */
  void forget(MachineId id);
}
