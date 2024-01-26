package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellIdentityCallback;
import java.util.function.Function;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

class MachineShellAuthenticationInitialStep extends MachineShellAuthenticationStep {

  private final Function<String, MachineShellUser> userGetter;
  private final Function<MachineShellPasswordName, char[]> passwordGetter;

  MachineShellAuthenticationInitialStep(
      MachineShellAuthenticationStepChanger stepChanger,
      Function<String, MachineShellUser> userGetter,
      Function<MachineShellPasswordName, char[]> passwordGetter) {
    super(stepChanger);
    this.userGetter = requireNonNull(userGetter);
    this.passwordGetter = requireNonNull(passwordGetter);
  }

  @Override
  void handle(NameCallback callback) {
    callback.setName(ofNullable(userGetter.apply(callback.getName()))
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
      stepChanger.credentials(passwordGetter, user);
      return user.getRemoteName();
    };
  }

  private Function<String, String> unknownUser() {
    return username -> {
      stepChanger.unknownUser(username);
      return username;
    };
  }
}
