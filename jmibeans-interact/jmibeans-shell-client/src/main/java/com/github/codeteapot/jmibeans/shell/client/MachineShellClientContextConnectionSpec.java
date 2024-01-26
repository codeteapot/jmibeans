package com.github.codeteapot.jmibeans.shell.client;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import java.util.Optional;

public interface MachineShellClientContextConnectionSpec {

  String getHostAddress();

  Optional<Integer> getPort();

  String getUsername();

  Optional<MachineShellIdentityName> getIdentityOnly();

  Optional<char[]> getPassword();
}
