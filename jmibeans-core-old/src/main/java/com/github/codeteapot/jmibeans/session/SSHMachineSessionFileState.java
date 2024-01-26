package com.github.codeteapot.jmibeans.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface SSHMachineSessionFileState {

  InputStream getInputStream() throws IOException;

  OutputStream getOutputStream() throws IOException;

  boolean detach();
}
