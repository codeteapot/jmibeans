package com.github.codeteapot.jmibeans.session;

/**
 * Authentication process for a user to establish a machine session.
 *
 * @see MachineSessionFactory#getSession(
 *   java.net.InetAddress,
 *   Integer,
 *   String,
 *   MachineSessionAuthentication)
 */
public interface MachineSessionAuthentication {

  /**
   * Performs the necessary actions on the authentication context to authenticate the user.
   *
   * @param context The context in which authentication is performed.
   */
  void authenticate(MachineSessionAuthenticationContext context);
}
