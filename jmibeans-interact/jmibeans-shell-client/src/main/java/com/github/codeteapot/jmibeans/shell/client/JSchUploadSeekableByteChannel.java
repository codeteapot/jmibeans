package com.github.codeteapot.jmibeans.shell.client;

import static com.jcraft.jsch.ChannelSftp.OVERWRITE;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

class JSchUploadSeekableByteChannel implements SeekableByteChannel {

  private final OutputStream output;
  private long pos;

  JSchUploadSeekableByteChannel(ChannelSftp jschChannel, String remotePath) throws IOException {
    try {
      output = jschChannel.put(remotePath, OVERWRITE);
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
    throw new UnsupportedOperationException();
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    byte[] buf = new byte[src.remaining()];
    src.get(buf);
    output.write(buf);
    int amount = buf.length;
    pos += amount;
    return amount;
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
    output.close(); // TODO State pattern: ClosedChannelException
  }
}
