package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Optional;

public class MachineShellUser implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String remoteName;
  private MachineShellIdentityName identityOnly;
  private MachineShellPasswordName passwordName;

  @ConstructorProperties("remoteName")
  public MachineShellUser(String remoteName) {
    this(remoteName, null, null);
  }

  @ConstructorProperties({"remoteName", "identityOnly", "passwordName"})
  public MachineShellUser(
      String remoteName,
      MachineShellIdentityName identityOnly,
      MachineShellPasswordName passwordName) {
    this.remoteName = requireNonNull(remoteName);
    this.identityOnly = identityOnly;
    this.passwordName = passwordName;
  }

  public String getRemoteName() {
    return remoteName;
  }

  public Optional<MachineShellIdentityName> getIdentityOnly() {
    return ofNullable(identityOnly);
  }

  public void setIdentityOnly(MachineShellIdentityName identityOnly) {
    this.identityOnly = identityOnly;
  }

  public Optional<MachineShellPasswordName> getPasswordName() {
    return ofNullable(passwordName);
  }

  public void setPasswordName(MachineShellPasswordName passwordName) {
    this.passwordName = passwordName;
  }
}
