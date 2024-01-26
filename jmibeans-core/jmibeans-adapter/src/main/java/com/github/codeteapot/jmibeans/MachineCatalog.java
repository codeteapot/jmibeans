package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.profile.MachineProfile;
import java.util.Optional;

/**
 * Catalog of machine profiles available on the platform.
 *
 * <p>The platform machines made available by the {@link PlatformPort}s have a profile name, which
 * is determined by {@link MachineLink#getProfileName()}. The catalog is responsible for resolving
 * the corresponding {@link MachineProfile} based on this name.
 *
 * @see PlatformAdapter
 */
public interface MachineCatalog {

  /**
   * Resolves a machine profile based on its name.
   *
   * @param profileName The profile name.
   *
   * @return The resolved profile, or empty in case the catalog does not recognize any profile with
   *         the indicated name.
   */
  Optional<MachineProfile> getProfile(MachineProfileName profileName);
}
