package com.github.codeteapot.jmibeans.shell.client.pool;

interface PooledMachineShellClientConnectionStateChanger {

  void changeState(PooledMachineShellClientConnectionState newState);
}
