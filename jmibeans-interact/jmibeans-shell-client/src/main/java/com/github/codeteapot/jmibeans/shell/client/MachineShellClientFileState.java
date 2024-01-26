package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class MachineShellClientFileState {

  protected final MachineShellClientFileStateChanger stateChanger;

  protected MachineShellClientFileState(MachineShellClientFileStateChanger stateChanger) {
    this.stateChanger = requireNonNull(stateChanger);
  }

  abstract InputStream getInputStream() throws IOException;

  abstract OutputStream getOutputStream() throws IOException;

  abstract boolean detach();
}
