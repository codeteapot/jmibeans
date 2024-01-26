package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

class JschMachineShellClientFile implements MachineShellClientFile {

  private MachineShellClientFileState state;

  JschMachineShellClientFile(ChannelSftp jschChannel, String path) throws JSchException {
    state = initial(newState -> state = requireNonNull(newState), jschChannel, path);
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return state.getInputStream();
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return state.getOutputStream();
  }

  boolean detach() {
    return state.detach();
  }

  private static MachineShellClientFileState initial(
      Consumer<MachineShellClientFileState> changeStateAction,
      ChannelSftp jschChannel,
      String path) throws JSchException {
    return new JschMachineShellClientFileAttachedState(
        new MachineShellClientFileStateChanger(changeStateAction),
        jschChannel,
        path);
  }
}
