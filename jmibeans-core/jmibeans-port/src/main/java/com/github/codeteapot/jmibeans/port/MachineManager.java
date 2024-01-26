package com.github.codeteapot.jmibeans.port;

public interface MachineManager {

  void accept(byte[] machineId, MachineLink link);

  void forget(byte[] machineId);
}
