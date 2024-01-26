package com.github.codeteapot.jmibeans.shell.client;

import java.nio.charset.Charset;

public interface MachineShellClientCommand<R> {

  String getStatement();

  MachineShellClientCommandExecution<R> getExecution(Charset charset);
}
