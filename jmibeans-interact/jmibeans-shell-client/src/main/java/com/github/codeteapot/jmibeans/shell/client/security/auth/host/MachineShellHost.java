package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import java.net.InetAddress;

public interface MachineShellHost {

  InetAddress getAddress();

  MachineShellHostKey getKey();
}
