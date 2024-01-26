package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import java.util.Optional;

public interface MachineShellUserRepository {

  Optional<MachineShellUser> getUser(String username);

  MachineShellPasswordRegistry getPasswordRegistry();
}
