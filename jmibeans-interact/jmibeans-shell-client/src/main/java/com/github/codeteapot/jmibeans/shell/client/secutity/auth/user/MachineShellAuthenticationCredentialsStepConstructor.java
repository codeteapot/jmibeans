package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import java.util.function.Function;

@FunctionalInterface
interface MachineShellAuthenticationCredentialsStepConstructor {

  MachineShellAuthenticationCredentialsStep construct(
      MachineShellAuthenticationStepChanger stepChanger,
      Function<MachineShellPasswordName, char[]> passwordGetter,
      MachineShellUser user);
}
