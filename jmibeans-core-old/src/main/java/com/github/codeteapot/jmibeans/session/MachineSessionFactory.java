package com.github.codeteapot.jmibeans.session;

import com.github.codeteapot.jmibeans.PlatformAdapter;
import java.net.InetAddress;

/**
 * Factory responsible for obtaining sessions established on a platform machine.
 *
 * <p>Implementations should include operations to provide identities and passwords that will be
 * used to authenticate users when establishing the session.
 *
 * @see PlatformAdapter
 */
public interface MachineSessionFactory {

  /**
   * Gets a machine session at the given address and port, for the indicated user, as well as the
   * authentication method used.
   *
   * <p>The session created does not necessarily imply that the corresponding physical connection
   * has been established. This method should always return an instance of a {@link MachineSession}
   * implementation. Error handling is delegated to the implementation of the session methods, which
   * have the possibility of throwing a {@link MachineSessionException} if they cannot be executed
   * successfully.
   *
   * @param host Address of the machine on which the session is established.
   * @param port Port used to establish the session or {@code null} if using the default.
   * @param username Name of the user for whom the session is established.
   * @param authentication Method used to authenticate the user.
   *
   * @return The session for the given user on the specified machine.
   */
  MachineSession getSession(
      InetAddress host,
      Integer port,
      String username,
      MachineSessionAuthentication authentication);
}
