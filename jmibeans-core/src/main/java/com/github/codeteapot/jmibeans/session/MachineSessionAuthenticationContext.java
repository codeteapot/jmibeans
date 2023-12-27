package com.github.codeteapot.jmibeans.session;

/**
 * Context in which an authentication process is performed to establish a machine session.
 *
 * <p>It gives the user the opportunity to provide the credentials necessary to authenticate.
 *
 * @see MachineSessionAuthentication#authenticate(MachineSessionAuthenticationContext)
 */
public interface MachineSessionAuthenticationContext {

  /**
   * Indicates that only the given identity should be used during the authentication process.
   *
   * <p>The rest of the registered identities should be ignored.
   *
   * <p>The session factory implementation is responsible for maintaining the named identities.
   *
   * @param identityName The identity name.
   */
  void setIdentityOnly(MachineSessionIdentityName identityName);

  /**
   * Add the password with the given name.
   *
   * <p>The session factory implementation is responsible for maintaining named passwords.
   *
   * @param passwordName The password name.
   */
  void addPassword(MachineSessionPasswordName passwordName);
}
