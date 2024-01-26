package com.github.codeteapot.jmibeans.port.docker;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineProfileName;

interface DockerMonitor {

  void start(MachineProfileName profileName, DockerMachineAgent agent);

  void stop();

  void destroy();

  void connect(MachineNetwork network);

  void disconnect(MachineNetworkName networkName);
}
