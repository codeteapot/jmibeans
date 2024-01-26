package com.github.codeteapot.jmibeans.shell.client;

import static java.util.logging.Logger.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

class MachineShellClientFileDetachedState extends MachineShellClientFileState {

  private static final Logger logger = getLogger(MachineShellClientFile.class.getName());

  MachineShellClientFileDetachedState(MachineShellClientFileStateChanger stateChanger) {
    super(stateChanger);
  }

  @Override
  InputStream getInputStream() throws IOException {
    throw new IOException("File detached");
  }

  @Override
  OutputStream getOutputStream() throws IOException {
    throw new IOException("File detached");
  }

  @Override
  boolean detach() {
    logger.warning("File already detached");
    return true;
  }
}
