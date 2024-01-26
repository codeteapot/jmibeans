package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellIdentityCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

class MachineShellAuthenticationCredentialsStep extends MachineShellAuthenticationStep {

  private final MachineShellPasswordRegistry passwordRegistry;
  private final MachineShellUser user;

  MachineShellAuthenticationCredentialsStep(
      MachineShellAuthenticationStepChanger stepChanger,
      MachineShellPasswordRegistry passwordRegistry,
      MachineShellUser user) {
    super(stepChanger);
    this.passwordRegistry = requireNonNull(passwordRegistry);
    this.user = requireNonNull(user);
  }

  @Override
  void handle(NameCallback callback) {
    throw new IllegalStateException("The username has already been specified");
  }

  @Override
  void handle(MachineShellIdentityCallback callback) {
    user.getIdentityOnly()
        .ifPresent(callback::setIdentityOnly);
  }

  @Override
  void handle(PasswordCallback callback) {
    user.getPassword()
        .flatMap(passwordRegistry::getPassword)
        .ifPresent(callback::setPassword);
  }
}
