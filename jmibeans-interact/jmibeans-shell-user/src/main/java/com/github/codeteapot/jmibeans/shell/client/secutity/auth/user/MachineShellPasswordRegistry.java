package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import java.util.Optional;

public interface MachineShellPasswordRegistry {

  Optional<char[]> getPassword(MachineShellPasswordName name);
}
