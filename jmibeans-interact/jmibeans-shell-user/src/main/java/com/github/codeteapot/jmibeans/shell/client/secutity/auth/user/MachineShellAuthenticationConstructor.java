package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

@FunctionalInterface
interface MachineShellAuthenticationConstructor {

  MachineShellAuthentication construct(MachineShellUserRepository repository);
}
