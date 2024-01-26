package com.github.codeteapot.jmibeans.shell.client.pool;

interface MachineShellConnectionStateChanger {

  void changeState(MachineShellConnectionState newState);
}
