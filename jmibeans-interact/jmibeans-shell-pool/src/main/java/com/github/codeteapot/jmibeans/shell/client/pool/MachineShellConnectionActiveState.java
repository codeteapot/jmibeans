package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.MachineShellFile;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;

class MachineShellConnectionActiveState extends MachineShellConnectionState {

  private final PooledMachineShellClientConnection pooled;
  private final MachineShellConnectionClosedStateConstructor closedStateConstructor;

  MachineShellConnectionActiveState(
      MachineShellConnectionStateChanger stateChanger,
      PooledMachineShellClientConnection pooled) {
    this(stateChanger, pooled, MachineShellConnectionClosedState::new);
  }

  MachineShellConnectionActiveState(
      MachineShellConnectionStateChanger stateChanger,
      PooledMachineShellClientConnection pooled,
      MachineShellConnectionClosedStateConstructor closedStateConstructor) {
    super(stateChanger);
    this.pooled = requireNonNull(pooled);
    this.closedStateConstructor = requireNonNull(closedStateConstructor);
  }

  @Override
  <R> R execute(MachineShellCommand<R> command)
      throws MachineShellException, MachineShellCommandExecutionException {
    try {
      return pooled.execute(new MachineShellCommandWrapper<>(command));
    } catch (MachineShellClientException e) {
      throw new MachineShellException(e);
    } catch (MachineShellClientCommandExecutionException e) {
      throw new MachineShellCommandExecutionException(e);
    }
  }

  @Override
  MachineShellFile file(String path) throws MachineShellException {
    try {
      return new MachineShellClientFileWrapper(pooled.file(path));
    } catch (MachineShellClientException e) {
      throw new MachineShellException(e);
    }
  }

  @Override
  void close() throws Exception {
    stateChanger.changeState(closedStateConstructor.construct(stateChanger));
    pooled.release();
  }
}
