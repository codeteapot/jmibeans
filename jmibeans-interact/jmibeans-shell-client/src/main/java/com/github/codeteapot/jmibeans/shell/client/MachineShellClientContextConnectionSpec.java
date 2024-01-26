package com.github.codeteapot.jmibeans.shell.client;

import java.util.Optional;

import com.github.codeteapot.jmibeans.shell.client.secutity.auth.MachineShellIdentityName;

public interface MachineShellClientContextConnectionSpec {

  String getHostAddress();

  Optional<Integer> getPort();

  String getUsername();

  Optional<MachineShellIdentityName> getIdentityOnly();

  Optional<char[]> getPassword();
}
