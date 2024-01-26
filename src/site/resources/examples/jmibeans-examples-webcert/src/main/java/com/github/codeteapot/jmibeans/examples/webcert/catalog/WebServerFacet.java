package com.github.codeteapot.jmibeans.examples.webcert.catalog;

import static com.github.codeteapot.jmibeans.shell.MachineShellCommand.shellCommand;
import static com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory.DEFAULT_USERNAME;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.ignoreOutput;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.readOutputString;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.returnNull;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.statelessShellCommandExecution;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.throwExceptionIf;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.withoutInput;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.isEqual;

import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;

public class WebServerFacet {

  private static final String DEFAULT_SERVER_NAME_CONF_NAME = "default-server-name";
  private static final String SERVER_NAME_CONF_NAME = "server-name";

  private final MachineShellConnectionFactory connectionFactory;
  private final String siteName;

  public WebServerFacet(
      MachineShellConnectionFactory connectionFactory,
      String availableConfDir,
      String serverName,
      String siteName) throws MachineBuildingException {
    this.connectionFactory = requireNonNull(connectionFactory);
    this.siteName = requireNonNull(siteName);
    updateServerName(availableConfDir, serverName);
  }

  public void enableSite() {
    try (MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME)) {
      connection.execute(enableSiteCommand());
      connection.execute(restartServiceCommand());
    } catch (MachineShellException e) {
      throw new IllegalStateException(e);
    }
  }

  public void disableSite() {
    try (MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME)) {
      connection.execute(disableSiteCommand());
      connection.execute(restartServiceCommand());
    } catch (MachineShellException e) {
      throw new IllegalStateException(e);
    }
  }

  private void updateServerName(String availableConfDir, String serverName)
      throws MachineBuildingException {
    try (MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME)) {
      connection.execute(updateServerNameCommand(availableConfDir, serverName));
      connection.execute(disableDefaultConfCommand());
      connection.execute(enableConfCommand());
      connection.execute(restartServiceCommand());
    } catch (MachineShellException e) {
      throw new MachineBuildingException(e);
    }
  }

  private MachineShellCommand<Void> updateServerNameCommand(
      String availableConfDir,
      String serverName) {
    return shellCommand( // TODO Implement using file system
        new StringBuilder()
            .append("tempconffile=$(mktemp)")
            .append(" && ")
            .append("echo 'ServerName ").append(serverName).append("' ").append(">$tempconffile")
            .append(" && ")
            .append("sudo mv $tempconffile ").append(availableConfDir).append('/')
            .append(SERVER_NAME_CONF_NAME).append(".conf")
            .toString(),
        statelessShellCommandExecution(
            ignoreOutput(),
            readOutputString().map(Exception::new),
            withoutInput(),
            throwExceptionIf(isEqual(0).negate(), returnNull())));
  }

  private MachineShellCommand<Void> disableDefaultConfCommand() {
    return shellCommand(
        new StringBuilder()
            .append("sudo a2disconf ").append(DEFAULT_SERVER_NAME_CONF_NAME)
            .toString(),
        statelessShellCommandExecution(
            ignoreOutput(),
            readOutputString().map(Exception::new),
            withoutInput(),
            throwExceptionIf(isEqual(0).negate(), returnNull())));
  }

  private MachineShellCommand<Void> enableConfCommand() {
    return shellCommand(
        new StringBuilder()
            .append("sudo a2enconf ").append(SERVER_NAME_CONF_NAME)
            .toString(),
        statelessShellCommandExecution(
            ignoreOutput(),
            readOutputString().map(Exception::new),
            withoutInput(),
            throwExceptionIf(isEqual(0).negate(), returnNull())));
  }

  private MachineShellCommand<Void> disableSiteCommand() {
    return shellCommand(
        new StringBuilder()
            .append("sudo a2dissite ").append(siteName)
            .toString(),
        statelessShellCommandExecution(
            ignoreOutput(),
            readOutputString().map(Exception::new),
            withoutInput(),
            throwExceptionIf(isEqual(0).negate(), returnNull())));
  }

  private MachineShellCommand<Void> enableSiteCommand() {
    return shellCommand(
        new StringBuilder()
            .append("sudo a2ensite ").append(siteName)
            .toString(),
        statelessShellCommandExecution(
            ignoreOutput(),
            readOutputString().map(Exception::new),
            withoutInput(),
            throwExceptionIf(isEqual(0).negate(), returnNull())));
  }

  private MachineShellCommand<Void> restartServiceCommand() {
    return shellCommand(
        "sudo apache2ctl restart",
        statelessShellCommandExecution(
            ignoreOutput(),
            readOutputString().map(Exception::new),
            withoutInput(),
            throwExceptionIf(isEqual(0).negate(), returnNull())));
  }
}
