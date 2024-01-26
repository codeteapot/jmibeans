package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import java.net.InetAddress;

class CallbackMachineShellHost implements MachineShellHost {

  private final MachineShellHostCallback callback;

  CallbackMachineShellHost(MachineShellHostCallback callback) {
    this.callback = requireNonNull(callback);
  }

  @Override
  public InetAddress getAddress() {
    return callback.getAddress();
  }

  @Override
  public MachineShellHostKey getKey() {
    return callback.getKey();
  }
}
