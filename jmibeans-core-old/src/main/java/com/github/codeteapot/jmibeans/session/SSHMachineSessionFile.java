package com.github.codeteapot.jmibeans.session;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class SSHMachineSessionFile implements MachineSessionFile, SSHMachineSessionFileStateChanger {

  private SSHMachineSessionFileState state;

  SSHMachineSessionFile(ChannelSftp jschChannel, String path) throws JSchException {
    state = new SSHMachineSessionFileAttached(this, jschChannel, path);
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
  public void changeState(SSHMachineSessionFileState newState) {
    state = newState;
  }

  boolean detach() {
    return state.detach();
  }
}
