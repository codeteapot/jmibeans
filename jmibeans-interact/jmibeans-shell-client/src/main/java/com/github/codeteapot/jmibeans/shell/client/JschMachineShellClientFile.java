package com.github.codeteapot.jmibeans.shell.client;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class JschMachineShellClientFile implements MachineShellClientFile, MachineShellClientFileStateChanger {

  private MachineShellClientFileState state;

  JschMachineShellClientFile(ChannelSftp jschChannel, String path) throws JSchException {
    state = new JschMachineShellClientFileAttachedState(this, jschChannel, path);
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return state.getInputStream();
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return state.getOutputStream();
  }

  @Override
  public void changeState(MachineShellClientFileState newState) {
    state = newState;
  }

  boolean detach() {
    return state.detach();
  }
}
