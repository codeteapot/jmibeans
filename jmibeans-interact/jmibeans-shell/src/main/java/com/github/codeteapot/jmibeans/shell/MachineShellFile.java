package com.github.codeteapot.jmibeans.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MachineShellFile {

  InputStream getInputStream() throws IOException;

  OutputStream getOutputStream() throws IOException;
}
