package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellIdentityCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

abstract class MachineShellAuthenticationStep {

  protected final MachineShellAuthenticationStepChanger stepChanger;

  protected MachineShellAuthenticationStep(MachineShellAuthenticationStepChanger stepChanger) {
    this.stepChanger = requireNonNull(stepChanger);
  }

  abstract void handle(NameCallback callback);

  abstract void handle(MachineShellIdentityCallback callback);

  abstract void handle(PasswordCallback callback);
}
