package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.MachineShellFile;

class MachineShellConnectionImpl
    implements MachineShellConnection, MachineShellConnectionStateChanger {

  private MachineShellConnectionState state;

  MachineShellConnectionImpl(PooledMachineShellClientConnection pooled) {
    this(stateChanger -> new MachineShellConnectionActiveState(stateChanger, pooled));
  }

  MachineShellConnectionImpl(MachineShellConnectionStateMapper stateMapper) {
    state = stateMapper.map(this);
  }

  @Override
  public <R> R execute(MachineShellCommand<R> command)
      throws MachineShellException, MachineShellCommandExecutionException {
    return state.execute(command);
  }

  @Override
  public MachineShellFile file(String path) throws MachineShellException {
    return state.file(path);
  }

  @Override
  public void close() throws Exception {
    state.close();
  }

  // Covered on PoolingMachineShellConnectionFactoryAcceptanceTest
  @Override
  public void changeState(MachineShellConnectionState newState) {
    state = requireNonNull(newState);
  }
}
