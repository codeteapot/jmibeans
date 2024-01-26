package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellIdentityCallback;
import java.util.function.Function;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

class MachineShellAuthenticationInitialStep extends MachineShellAuthenticationStep {

  private final MachineShellUserRepository repository;

  MachineShellAuthenticationInitialStep(
      MachineShellAuthenticationStepChanger stepChanger,
      MachineShellUserRepository repository) {
    super(stepChanger);
    this.repository = requireNonNull(repository);
  }

  @Override
  void handle(NameCallback callback) {
    callback.setName(repository.getUser(callback.getName())
        .map(this::knownUser)
        .orElseGet(this::unknownUser)
        .apply(callback.getName()));
  }

  @Override
  void handle(MachineShellIdentityCallback callback) {
    throw new IllegalStateException("The username has not yet been specified");
  }

  @Override
  void handle(PasswordCallback callback) {
    throw new IllegalStateException("The username has not yet been specified");
  }

  private Function<String, String> knownUser(MachineShellUser user) {
    return username -> {
      stepChanger.credentials(repository.getPasswordRegistry(), user);
      return user.getRemoteName().orElse(username);
    };
  }

  private Function<String, String> unknownUser() {
    return username -> {
      stepChanger.unknownUser(username);
      return username;
    };
  }
}
