package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

@FunctionalInterface
interface MachineShellAuthenticationCredentialsStepConstructor {

  MachineShellAuthenticationCredentialsStep construct(
      MachineShellAuthenticationStepChanger stepChanger,
      MachineShellPasswordRegistry passwordRegistry,
      MachineShellUser user);
}
