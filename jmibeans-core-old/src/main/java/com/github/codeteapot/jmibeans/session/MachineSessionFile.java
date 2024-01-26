package com.github.codeteapot.jmibeans.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Access to a remote file on a platform machine.
 *
 * @see MachineSession#file(String)
 */
public interface MachineSessionFile {

  /**
   * Open a stream to read the file.
   *
   * @return The stream for reading the file.
   *
   * @throws IOException In case an error occurs when opening the stream.
   */
  InputStream getInputStream() throws IOException;

  /**
   * Open a stream to write to the file.
   *
   * @return The a stream to write over the file.
   *
   * @throws IOException In case an error occurs when opening the stream.
   */
  OutputStream getOutputStream() throws IOException;
}
