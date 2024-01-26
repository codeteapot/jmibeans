package com.github.codeteapot.jmibeans.shell;

import java.nio.charset.Charset;

public interface MachineShellCommand<R> {

  String getStatement();

  MachineShellCommandExecution<R> getExecution(Charset charset);
}
