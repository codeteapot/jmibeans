package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import java.util.function.Function;

class MachineShellAuthenticationStepChanger {

  private final Consumer<MachineShellAuthenticationStep> changeStepAction;
  private final MachineShellAuthenticationCredentialsStepConstructor credentialsStepConstructor;
  private final MachineShellAuthenticationUnknownUserStepConstructor unknownUserStepConstructor;

  MachineShellAuthenticationStepChanger(
      Consumer<MachineShellAuthenticationStep> changeStepAction,
      MachineShellAuthenticationCredentialsStepConstructor credentialsStepConstructor,
      MachineShellAuthenticationUnknownUserStepConstructor unknownUserStepConstructor) {
    this.changeStepAction = requireNonNull(changeStepAction);
    this.credentialsStepConstructor = requireNonNull(credentialsStepConstructor);
    this.unknownUserStepConstructor = requireNonNull(unknownUserStepConstructor);
  }

  void credentials(
      Function<MachineShellPasswordName, char[]> passwordGetter,
      MachineShellUser user) {
    changeStepAction.accept(credentialsStepConstructor.construct(this, passwordGetter, user));
  }

  void unknownUser(String username) {
    changeStepAction.accept(unknownUserStepConstructor.construct(this, username));
  }
}
