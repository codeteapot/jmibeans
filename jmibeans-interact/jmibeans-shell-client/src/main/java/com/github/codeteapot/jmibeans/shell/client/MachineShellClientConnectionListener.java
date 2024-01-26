package com.github.codeteapot.jmibeans.shell.client;

import java.util.EventListener;

public interface MachineShellClientConnectionListener extends EventListener {
  
  void connectionClosed(MachineShellClientConnectionEvent event);
  
  void connectionErrorOccurred(MachineShellClientConnectionEvent event);
}
