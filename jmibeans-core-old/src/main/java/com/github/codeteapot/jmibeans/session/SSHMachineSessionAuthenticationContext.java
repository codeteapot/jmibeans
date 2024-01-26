package com.github.codeteapot.jmibeans.session;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import com.jcraft.jsch.Session;
import java.util.function.Consumer;
import java.util.logging.Logger;

class SSHMachineSessionAuthenticationContext implements MachineSessionAuthenticationContext {

  private static final Logger logger = getLogger(
      SSHMachineSessionAuthenticationContext.class.getName());

  private final Session jschSession;
  private final SSHMachineSessionPasswordMapper passwordMapper;

  SSHMachineSessionAuthenticationContext(
      Session jschSession,
      SSHMachineSessionPasswordMapper passwordMapper) {
    this.jschSession = requireNonNull(jschSession);
    this.passwordMapper = requireNonNull(passwordMapper);
  }

  @Override
  public void setIdentityOnly(MachineSessionIdentityName identityName) {
    logger.warning("Not implemented. Using any known identity");
  }

  @Override
  public void addPassword(MachineSessionPasswordName passwordName) {
    passwordMapper.map(passwordName)
        .map(this::passwordFound)
        .orElseGet(this::passwordNotFound)
        .accept(passwordName);
  }

  private Consumer<MachineSessionPasswordName> passwordFound(byte[] password) {
    return passwordName -> jschSession.setPassword(password);
  }

  private Consumer<MachineSessionPasswordName> passwordNotFound() {
    return passwordName -> logger.warning(new StringBuilder()
        .append("Password ").append(passwordName).append(" was not found")
        .toString());
  }
}
