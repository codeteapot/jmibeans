package com.github.codeteapot.jmibeans.shell.client.pool;

import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommand;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;

class PooledMachineShellClientConnectionAnyState extends PooledMachineShellClientConnectionState {

  PooledMachineShellClientConnectionAnyState(
      PooledMachineShellClientConnectionStateChanger stateChanger,
      PooledMachineShellClientConnectionClosedStateConstructor closedStateConstructor,
      PooledMachineShellClientConnectionErrorOccurredStateConstructor errStateConstructor) {
    super(stateChanger, closedStateConstructor, errStateConstructor);
  }

  @Override
  boolean request(String username) {
    throw new UnsupportedOperationException();
  }

  @Override
  void acquire() {
    throw new UnsupportedOperationException();
  }

  @Override
  <R> R execute(MachineShellClientCommand<R> command) throws MachineShellClientException,
      MachineShellClientCommandExecutionException {
    throw new UnsupportedOperationException();
  }

  @Override
  MachineShellClientFile file(String path) throws MachineShellClientException {
    throw new UnsupportedOperationException();
  }

  @Override
  void closeNow() {
    throw new UnsupportedOperationException();
  }

  @Override
  void release() {
    throw new UnsupportedOperationException();
  }
}
