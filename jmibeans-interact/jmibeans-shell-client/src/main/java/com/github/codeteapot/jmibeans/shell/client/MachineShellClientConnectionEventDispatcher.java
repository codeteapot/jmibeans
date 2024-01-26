package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

class MachineShellClientConnectionEventDispatcher
    implements MachineShellClientConnectionEventSource {

  private final MachineShellClientConnection connection;
  private final List<MachineShellClientConnectionListener> listeners;

  MachineShellClientConnectionEventDispatcher(MachineShellClientConnection connection) {
    this.connection = requireNonNull(connection);
    listeners = new ArrayList<>();
  }

  public void addConnectionEventListener(MachineShellClientConnectionListener listener) {
    listeners.add(listener);
  }

  public void removeConnectionEventListener(MachineShellClientConnectionListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void fireClosedEvent() {
    listeners.forEach(listener -> listener.connectionClosed(
        new MachineShellClientConnectionEvent(connection)));
  }

  @Override
  public void fireErrorOccurredEvent(MachineShellClientException exception) {
    listeners.forEach(listener -> listener.connectionErrorOccurred(
        new MachineShellClientConnectionEvent(connection, exception)));
  }
}
