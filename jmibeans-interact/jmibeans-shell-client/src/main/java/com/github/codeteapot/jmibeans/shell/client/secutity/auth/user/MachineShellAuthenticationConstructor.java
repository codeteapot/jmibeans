package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import java.util.function.Function;

@FunctionalInterface
interface MachineShellAuthenticationConstructor {

  MachineShellAuthentication construct(
      Function<String, MachineShellUser> userGetter,
      Function<MachineShellPasswordName, char[]> passwordGetter);
}
