package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.nio.charset.Charset;

class CertificateRemoveCommand implements MachineShellCommand<Void> {

  private final String path;
  private final String keyPath;

  CertificateRemoveCommand(String path, String keyPath) {
    this.path = requireNonNull(path);
    this.keyPath = requireNonNull(keyPath);
  }

  @Override
  public String getStatement() {
    return new StringBuilder()
        .append("rm ").append(path)
        .append(" && ")
        .append("rm ").append(keyPath)
        .toString();
  }

  @Override
  public MachineShellCommandExecution<Void> getExecution(Charset charset) {
    return new CertificateRemoveCommandExecution(charset);
  }
}
