package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import java.util.Optional;

class TestMachineShellClientContextConnectionSpec implements
    MachineShellClientContextConnectionSpec {

  private final String hostAddress;
  private final Integer port;
  private final String username;
  private final MachineShellIdentityName identityOnly;
  private final char[] password;

  TestMachineShellClientContextConnectionSpec(String hostAddress, int port, String username) {
    this.hostAddress = requireNonNull(hostAddress);
    this.port = port;
    this.username = requireNonNull(username);
    identityOnly = null;
    password = null;
  }

  TestMachineShellClientContextConnectionSpec(String hostAddress, String username) {
    this.hostAddress = requireNonNull(hostAddress);
    port = null;
    this.username = requireNonNull(username);
    identityOnly = null;
    password = null;
  }

  TestMachineShellClientContextConnectionSpec(
      String hostAddress,
      int port,
      String username,
      MachineShellIdentityName identityOnly) {
    this.hostAddress = requireNonNull(hostAddress);
    this.port = port;
    this.username = requireNonNull(username);
    this.identityOnly = requireNonNull(identityOnly);
    password = null;
  }

  TestMachineShellClientContextConnectionSpec(
      String hostAddress,
      int port,
      String username,
      char[] password) {
    this.hostAddress = requireNonNull(hostAddress);
    this.port = port;
    this.username = requireNonNull(username);
    identityOnly = null;
    this.password = requireNonNull(password);
  }

  @Override
  public String getHostAddress() {
    return hostAddress;
  }

  @Override
  public Optional<Integer> getPort() {
    return ofNullable(port);
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public Optional<MachineShellIdentityName> getIdentityOnly() {
    return ofNullable(identityOnly);
  }

  @Override
  public Optional<char[]> getPassword() {
    return ofNullable(password);
  }
}
