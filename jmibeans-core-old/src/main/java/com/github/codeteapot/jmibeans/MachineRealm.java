package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.session.MachineSessionAuthentication;
import java.util.Optional;

/**
 * Set of recognized users of a machine.
 *
 * <p>Determines the authentication method used by each recognized user.
 *
 * @see MachineProfile#getRealm()
 */
public interface MachineRealm {

  /**
   * Returns the authentication method for the user with the given name, if it exists.
   *
   * @param username The username from which the authentication method should be obtained.
   *
   * @return The authentication method of the given user, or empty if not recognized.
   */
  Optional<MachineSessionAuthentication> authentication(String username);
}
