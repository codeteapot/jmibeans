package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

@FunctionalInterface
interface MachineShellAuthenticationUnknownUserStepConstructor {

  MachineShellAuthenticationUnknownUserStep construct(
      MachineShellAuthenticationStepChanger stepChanger,
      String username);
}
