package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellIdentityCallback;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

class MachineShellAuthentication {

  private MachineShellAuthenticationStep step;

  MachineShellAuthentication(MachineShellUserRepository repository) {
    this(changeStepAction -> initial(changeStepAction, repository));
  }

  MachineShellAuthentication(Function< //
      Consumer<MachineShellAuthenticationStep>, MachineShellAuthenticationStep> stepMapper) {
    step = stepMapper.apply(newStep -> step = requireNonNull(newStep));
  }

  void handle(NameCallback callback) {
    step.handle(callback);
  }

  void handle(MachineShellIdentityCallback callback) {
    step.handle(callback);
  }

  void handle(PasswordCallback callback) {
    step.handle(callback);
  }

  private static MachineShellAuthenticationStep initial(
      Consumer<MachineShellAuthenticationStep> changeStepAction,
      MachineShellUserRepository repository) {
    return new MachineShellAuthenticationInitialStep(new MachineShellAuthenticationStepChanger(
        changeStepAction,
        MachineShellAuthenticationCredentialsStep::new,
        MachineShellAuthenticationUnknownUserStep::new), repository);
  }
}
