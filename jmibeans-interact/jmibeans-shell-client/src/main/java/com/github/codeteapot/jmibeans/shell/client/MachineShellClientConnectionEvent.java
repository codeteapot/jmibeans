package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Optional.ofNullable;

import java.util.EventObject;
import java.util.Optional;

public class MachineShellClientConnectionEvent extends EventObject {

  private static final long serialVersionUID = 1L;

  private final MachineShellClientException exception;

  public MachineShellClientConnectionEvent(MachineShellClientConnection connection) {
    this(connection, null);
  }

  public MachineShellClientConnectionEvent(
      MachineShellClientConnection connection,
      MachineShellClientException exception) {
    super(connection);
    this.exception = exception;
  }

  public Optional<MachineShellClientException> getClientException() {
    return ofNullable(exception);
  }
}
