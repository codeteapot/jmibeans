package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellIdentityCallback;
import java.util.logging.Logger;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

class MachineShellAuthenticationUnknownUserStep extends MachineShellAuthenticationStep {

  private static final Logger logger = getLogger(MachineShellAuthentication.class.getName());

  private final String username;

  MachineShellAuthenticationUnknownUserStep(
      MachineShellAuthenticationStepChanger stepChanger,
      String username) {
    super(stepChanger);
    this.username = requireNonNull(username);
  }

  @Override
  void handle(NameCallback callback) {
    throw new IllegalStateException("The username has already been specified");
  }

  @Override
  void handle(MachineShellIdentityCallback callback) {
    logger.warning(new StringBuilder()
        .append("User ").append(username).append(" is not known")
        .toString());
  }

  @Override
  void handle(PasswordCallback callback) {
    logger.warning(new StringBuilder()
        .append("User ").append(username).append(" is not known")
        .toString());
  }
}
