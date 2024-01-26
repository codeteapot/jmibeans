package com.github.codeteapot.jmibeans.machine;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.MachineBuilderContext;
import com.github.codeteapot.jmibeans.MachineRealm;
import java.beans.ConstructorProperties;

/**
 * Exception thrown when it is not possible to establish a machine session because the user is not
 * recognized.
 *
 * <p>A user is recognized if and only if the call to {@link MachineRealm#authentication(String)}
 * returns a non-empty authentication method.
 *
 * @see MachineBuilderContext#getSession(String)
 * @see MachineContext#getSession(String)
 */
public class UnknownUserException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Reference of the machine for which the user is not recognized.
   */
  private final MachineRef machineRef;
  
  /**
   * Name of the user not recognized.
   */
  private final String username;

  /**
   * Creates an unrecognized user exception on the specified machine.
   *
   * @param machineRef The reference of the machine in which the user is not recognized.
   * @param username The name of the user that has not been recognized.
   */
  @ConstructorProperties({
      "machineRef",
      "username"
  })
  public UnknownUserException(MachineRef machineRef, String username) {
    super(format("Unknown user %s on machine %s", username, machineRef));
    this.machineRef = requireNonNull(machineRef);
    this.username = requireNonNull(username);
  }
  
  /**
   * Reference of the machine for which the user is not recognized.
   *
   * @return The reference of the machine.
   */
  public MachineRef getMachineRef() {
    return machineRef;
  }

  /**
   * Name of the user not recognized.
   *
   * @return The name of the unrecognized user.
   */
  public String getUsername() {
    return username;
  }
}
