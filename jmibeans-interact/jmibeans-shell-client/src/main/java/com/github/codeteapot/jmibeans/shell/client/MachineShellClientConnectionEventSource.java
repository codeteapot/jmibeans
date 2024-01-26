package com.github.codeteapot.jmibeans.shell.client;

interface MachineShellClientConnectionEventSource {

  void fireClosedEvent();

  void fireErrorOccurredEvent(MachineShellClientException exception);
}
