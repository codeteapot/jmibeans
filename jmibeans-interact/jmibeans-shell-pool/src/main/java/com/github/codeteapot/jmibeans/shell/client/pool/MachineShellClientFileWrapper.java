package com.github.codeteapot.jmibeans.shell.client.pool;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellFile;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class MachineShellClientFileWrapper implements MachineShellFile {

  private final MachineShellClientFile wrapped;

  MachineShellClientFileWrapper(MachineShellClientFile wrapped) {
    this.wrapped = requireNonNull(wrapped);
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return wrapped.getInputStream();
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return wrapped.getOutputStream();
  }
}
