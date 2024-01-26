package com.github.codeteapot.jmibeans.depot.dns.catalog;

import static com.github.codeteapot.jmibeans.shell.MachineShellCommand.shellCommand;
import static com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory.DEFAULT_USERNAME;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.closeInput;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.ignoreOutput;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.readOutputString;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.returnNull;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.statelessShellCommandExecution;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.throwExceptionIf;
import static com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.withoutInput;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.isEqual;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.shell.MachineShellCommand;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import com.github.codeteapot.jmibeans.shell.StatelessMachineShellCommandExecution.StreamSupplier;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

class DNSService {

  private final MachineShellConnectionFactory connectionFactory;
  private final String zonesScript;
  private final DNSServiceAddress address;

  DNSService(
      MachineNetwork network,
      MachineShellConnectionFactory connectionFactory,
      String zonesScript) throws UnknownHostException {
    this.connectionFactory = requireNonNull(connectionFactory);
    this.zonesScript = requireNonNull(zonesScript);
    address = new DNSServiceAddress(network);
  }

  void enableZone(DNSZoneName zoneName) throws DNSServiceException {
    try (MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME)) {
      connection.execute(enableCommand(zoneName));
    } catch (MachineShellException e) {
      throw new DNSServiceException(e);
    }
  }

  void add(DNSZoneName zoneName, DNSHostName hostName, InetAddress address)
      throws DNSServiceException {
    try (MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME)) {
      connection.execute(updateCommand(updateAddInput(zoneName, hostName, address)));
    } catch (MachineShellException e) {
      throw new DNSServiceException(e);
    }
  }

  void delete(DNSZoneName zoneName, DNSHostName hostName) throws DNSServiceException {
    try (MachineShellConnection connection = connectionFactory.getConnection(DEFAULT_USERNAME)) {
      connection.execute(updateCommand(updateDeleteInput(zoneName, hostName)));
    } catch (MachineShellException e) {
      throw new DNSServiceException(e);
    }
  }

  private MachineShellCommand<Void> enableCommand(DNSZoneName zoneName) {
    return shellCommand(
        new StringBuilder()
            .append(zonesScript)
            .append(" enable") // TODO Should fail if it is not "enable"
            .append(' ').append(zoneName)
            .append(' ').append(address.getAddress())
            .toString(),
        statelessShellCommandExecution(
            ignoreOutput(),
            readOutputString().map(Exception::new),
            withoutInput(),
            throwExceptionIf(isEqual(0).negate(), returnNull())));
  }

  private MachineShellCommand<Void> updateCommand(StreamSupplier updateInput) {
    return shellCommand(
        "sudo -u bind nsupdate -v -L 3 -k /etc/bind/ns-rndc.key",
        statelessShellCommandExecution(
            ignoreOutput(),
            readOutputString().map(Exception::new),
            updateInput.andThen(closeInput()),
            throwExceptionIf(isEqual(0).negate(), returnNull())));
  }

  private StreamSupplier updateAddInput(
      DNSZoneName zoneName,
      DNSHostName hostName,
      InetAddress address) {
    return (charset, input) -> {
      try (PrintWriter writer = new PrintWriter(input)) {
        writer.println("server localhost");
        writer.print("zone ");
        writer.println(zoneName);
        writer.print("update add ");
        writer.print(hostName);
        writer.print(" 300 A ");
        writer.println(address.getHostAddress());
        writer.println("send");
      }
    };
  }

  private StreamSupplier updateDeleteInput(DNSZoneName zoneName, DNSHostName hostName) {
    return (charset, input) -> {
      try (PrintWriter writer = new PrintWriter(input)) {
        writer.println("server localhost");
        writer.print("zone ");
        writer.println(zoneName);
        writer.print("update delete ");
        writer.print(hostName);
        writer.print(" A");
        writer.println("send");
      }
    };
  }
}
