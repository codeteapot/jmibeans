package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import java.util.Optional;

public interface MachineShellUser {

  Optional<String> getRemoteName();

  Optional<MachineShellIdentityName> getIdentityOnly();

  Optional<MachineShellPasswordName> getPassword();
}
