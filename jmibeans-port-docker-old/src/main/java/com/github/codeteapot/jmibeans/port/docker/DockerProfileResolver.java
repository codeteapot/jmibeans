package com.github.codeteapot.jmibeans.port.docker;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import java.util.Optional;

/**
 * Determines the profile name of a machine based on the assigned role.
 *
 * <p>The role of a container is defined in the {@code "com.github.codeteapot.jmi.role"} label.
 * 
 * <p>If this label is not defined, or the machine profile cannot be determined by the role, the
 * default profile name is used.
 *
 * @see DockerPlatformPort
 */
public interface DockerProfileResolver {

  /**
   * Gets the default machine profile name.
   *
   * @return The default profile name.
   */
  MachineProfileName getDefault();

  /**
   * Gets the machine profile name based on the role.
   *
   * @param roleName The role of the container.
   *
   * @return The corresponding profile name, if any.
   */
  Optional<MachineProfileName> fromRole(String roleName);
}
