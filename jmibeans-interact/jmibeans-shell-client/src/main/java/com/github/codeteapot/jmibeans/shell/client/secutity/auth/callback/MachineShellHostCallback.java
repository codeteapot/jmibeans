package com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback;

import static com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback //
    .MachineShellHostStatus.UNKNOWN;
import static java.util.Objects.requireNonNull;

import java.net.InetAddress;

import javax.security.auth.callback.Callback;

public class MachineShellHostCallback implements Callback {

  private final InetAddress host;
  private final byte[] key;
  private MachineShellHostStatus status;

  public MachineShellHostCallback(InetAddress host, byte[] key) {
    this.host = requireNonNull(host);
    this.key = requireNonNull(key);
    status = UNKNOWN;
  }

  public InetAddress getHost() {
    return host;
  }

  public byte[] getKey() {
    return key;
  }

  public MachineShellHostStatus getStatus() {
    return status;
  }

  public void setStatus(MachineShellHostStatus status) {
    this.status = requireNonNull(status);
  }
}
