package com.github.codeteapot.jmibeans.shell.client;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

class JSchDownloadSeekableByteChannel implements SeekableByteChannel {

  private final InputStream input;
  private long pos;

  JSchDownloadSeekableByteChannel(ChannelSftp jschChannel, String remotePath) throws IOException {
    try {
      input = jschChannel.get(remotePath);
      pos = 0;
    } catch (SftpException e) {
      throw new IOException(e);
    }
  }

  @Override
  public boolean isOpen() {
    return true; // TODO State pattern
  }

  @Override
  public long size() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public long position() throws IOException {
    return pos;
  }

  @Override
  public int read(ByteBuffer dst) throws IOException {
    byte[] buf = new byte[dst.remaining()];
    int amount = input.read(buf);
    dst.put(buf);
    pos += amount;
    return amount;
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public SeekableByteChannel position(long newPosition) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public SeekableByteChannel truncate(long size) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws IOException {
    input.close(); // TODO State pattern: ClosedChannelException
  }
}
