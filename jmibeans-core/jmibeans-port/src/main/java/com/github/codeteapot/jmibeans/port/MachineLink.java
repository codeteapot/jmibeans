package com.github.codeteapot.jmibeans.port;

import com.github.codeteapot.jmibeans.machine.MachineAgent;

public interface MachineLink {

  MachineProfileName getProfileName();

  MachineAgent getAgent();
}
