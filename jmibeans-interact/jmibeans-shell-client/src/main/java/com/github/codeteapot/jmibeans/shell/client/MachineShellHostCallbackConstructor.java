package com.github.codeteapot.jmibeans.shell.client;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import java.net.InetAddress;

public interface MachineShellHostCallbackConstructor {

  MachineShellHostCallback construct(InetAddress address, MachineShellHostKey key);
}
