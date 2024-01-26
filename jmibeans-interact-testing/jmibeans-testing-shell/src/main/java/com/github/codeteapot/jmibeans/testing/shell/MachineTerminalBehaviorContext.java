package com.github.codeteapot.jmibeans.testing.shell;

import java.io.InputStream;
import java.io.OutputStream;

public interface MachineTerminalBehaviorContext {

  OutputStream getOutputStream();

  OutputStream getErrorStream();

  InputStream getInputStream();
}
