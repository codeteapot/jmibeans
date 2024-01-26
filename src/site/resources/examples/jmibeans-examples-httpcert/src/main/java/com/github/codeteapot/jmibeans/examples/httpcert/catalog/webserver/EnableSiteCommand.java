package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecution;
import java.nio.charset.Charset;

class EnableSiteCommand implements MachineShellCommand<Void> {

  private final String siteName;
  
  EnableSiteCommand(String siteName) {
    this.siteName = requireNonNull(siteName);
  }

  @Override
  public String getStatement() {
    return new StringBuilder()
        .append("sudo a2ensite ").append(siteName)
        .append(" && ")
        .append("sudo apache2ctl restart")
        .toString();
  }

  @Override
  public MachineShellCommandExecution<Void> getExecution(Charset charset) {
    return new EnableSiteCommandExecution(charset);
  }
}
