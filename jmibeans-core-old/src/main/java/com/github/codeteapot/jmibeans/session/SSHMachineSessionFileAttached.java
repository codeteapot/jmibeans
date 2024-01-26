package com.github.codeteapot.jmibeans.session;

import static java.util.Objects.requireNonNull;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class SSHMachineSessionFileAttached implements SSHMachineSessionFileState {

  private static final int CONNECTION_TIMEOUT = 1000;

  private final SSHMachineSessionFileStateChanger stateChanger;
  private final ChannelSftp jschChannel;
  private final String path;

  SSHMachineSessionFileAttached(
      SSHMachineSessionFileStateChanger stateChanger,
      ChannelSftp jschChannel,
      String path) throws JSchException {
    this.stateChanger = requireNonNull(stateChanger);
    this.jschChannel = requireNonNull(jschChannel);
    this.path = requireNonNull(path);
    jschChannel.connect(CONNECTION_TIMEOUT);
  }

  @Override
  public InputStream getInputStream() throws IOException {
    try {
      return jschChannel.get(path);
    } catch (SftpException e) {
      throw new IOException(e);
    }
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    try {
      return jschChannel.put(path);
    } catch (SftpException e) {
      throw new IOException(e);
    }
  }

  @Override
  public boolean detach() {
    jschChannel.disconnect();
    stateChanger.changeState(new SSHMachineSessionFileDetached());
    return true;
  }
}
