package com.github.codeteapot.jmibeans.session;

import static java.util.logging.Logger.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

class SSHMachineSessionFileDetached implements SSHMachineSessionFileState {

  private static final Logger logger = getLogger(SSHMachineSessionFileDetached.class.getName());

  SSHMachineSessionFileDetached() {}

  @Override
  public InputStream getInputStream() throws IOException {
    throw new IOException("File detached");
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    throw new IOException("File detached");
  }

  @Override
  public boolean detach() {
    logger.warning("File already detached");
    return true;
  }
}
