package com.github.codeteapot.jmibeans.examples.httpcert;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.user.MachineShellPasswordName;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.user.MachineShellPasswordRegistry;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.user.MachineShellUser;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.user.MachineShellUserRepository;
import java.util.Optional;

class StandaloneIndentityShellUser
    implements MachineShellUserRepository, MachineShellUser, MachineShellPasswordRegistry {

  private final String name;
  private final String remoteName;

  StandaloneIndentityShellUser(String name, String remoteName) {
    this.name = requireNonNull(name);
    this.remoteName = requireNonNull(remoteName);
  }

  @Override
  public Optional<MachineShellUser> getUser(String username) {
    return name.equals(username) ? Optional.of(this) : Optional.empty();
  }

  @Override
  public MachineShellPasswordRegistry getPasswordRegistry() {
    return this;
  }

  @Override
  public Optional<String> getRemoteName() {
    return Optional.of(remoteName);
  }

  @Override
  public Optional<MachineShellIdentityName> getIdentityOnly() {
    return Optional.empty();
  }

  @Override
  public Optional<MachineShellPasswordName> getPassword() {
    return Optional.empty();
  }

  @Override
  public Optional<char[]> getPassword(MachineShellPasswordName name) {
    return Optional.empty();
  }
}
