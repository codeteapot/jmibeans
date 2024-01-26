package com.github.codeteapot.jmibeans.shell.client.security.auth.callback;

import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.UNKNOWN;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import java.net.InetAddress;
import javax.security.auth.callback.Callback;

public class MachineShellHostCallback implements Callback {

  private final InetAddress address;
  private final MachineShellHostKey key;

  MachineShellHostStatus status;

  public MachineShellHostCallback(InetAddress address, MachineShellHostKey key) {
    this.address = requireNonNull(address);
    this.key = requireNonNull(key);
    status = UNKNOWN;
  }

  public InetAddress getAddress() {
    return address;
  }

  public MachineShellHostKey getKey() {
    return key;
  }

  public MachineShellHostStatus getStatus() {
    return status;
  }

  public void setStatus(MachineShellHostStatus status) {
    this.status = requireNonNull(status);
  }
}
