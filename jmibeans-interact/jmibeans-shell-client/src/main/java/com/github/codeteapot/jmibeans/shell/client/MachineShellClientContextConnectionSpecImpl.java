package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.net.InetAddress;
import java.util.Optional;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellIdentityCallback;

class MachineShellClientContextConnectionSpecImpl implements
    MachineShellClientContextConnectionSpec {

  private final InetAddress host;
  private final Integer port;
  private final NameCallback nameCallback;
  private final MachineShellIdentityCallback identityCallback;
  private final PasswordCallback passwordCallback;

  public MachineShellClientContextConnectionSpecImpl(
      InetAddress host,
      Integer port,
      NameCallback nameCallback,
      MachineShellIdentityCallback identityCallback,
      PasswordCallback passwordCallback) {
    this.host = requireNonNull(host);
    this.port = port;
    this.nameCallback = requireNonNull(nameCallback);
    this.identityCallback = requireNonNull(identityCallback);
    this.passwordCallback = requireNonNull(passwordCallback);
  }

  @Override
  public String getHostAddress() {
    return host.getHostAddress();
  }

  @Override
  public Optional<Integer> getPort() {
    return ofNullable(port);
  }

  @Override
  public String getUsername() {
    return nameCallback.getName();
  }

  @Override
  public Optional<MachineShellIdentityName> getIdentityOnly() {
    return identityCallback.getIdentityOnly();
  }

  @Override
  public Optional<char[]> getPassword() {
    return ofNullable(passwordCallback.getPassword());
  }
}
