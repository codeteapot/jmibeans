package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import java.util.Optional;

/**
 * Catalog of machine profiles used by a platform adapter.
 *
 * @see PlatformAdapter
 */
public interface MachineCatalog {

  /**
   * Gets the machine profile with the specified name, if it exists.
   *
   * @param profileName Name of the profile to be obtained.
   *
   * @return The profile with the specified name, or empty if it does not exist.
   */
  Optional<MachineProfile> getProfile(MachineProfileName profileName);
}
