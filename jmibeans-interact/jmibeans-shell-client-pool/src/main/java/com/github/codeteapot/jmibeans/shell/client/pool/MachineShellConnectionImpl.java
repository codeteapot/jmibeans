package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.nio.file.FileSystem;
import java.util.function.Consumer;
import java.util.function.Function;

class MachineShellConnectionImpl implements MachineShellConnection {

  private MachineShellConnectionState state;

  MachineShellConnectionImpl(PooledMachineShellClientConnection pooled) {
    this(changeStateAction -> initial(changeStateAction, pooled));
  }

  MachineShellConnectionImpl(
      Function<Consumer<MachineShellConnectionState>, MachineShellConnectionState> stateMapper) {
    state = stateMapper.apply(newState -> state = requireNonNull(newState));
  }

  @Override
  public FileSystem getFileSystem() throws MachineShellException {
    return state.getFileSystem();
  }

  @Override
  public <R> R execute(MachineShellCommand<R> command)
      throws MachineShellException, MachineShellCommandExecutionException {
    return state.execute(command);
  }

  @Override
  public void close() throws MachineShellException {
    state.close();
  }

  private static MachineShellConnectionState initial(
      Consumer<MachineShellConnectionState> changeStateAction,
      PooledMachineShellClientConnection pooled) {
    return new MachineShellConnectionActiveState(
        new MachineShellConnectionStateChanger(
            changeStateAction,
            MachineShellConnectionClosedState::new),
        pooled);
  }
}
