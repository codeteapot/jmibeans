package com.github.codeteapot.jmibeans.depot.dns.catalog;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class DNSServerFacet {

  private final MachineAgent agent;
  private final MachineShellConnectionFactory connectionFactory;
  private final Set<DNSZoneSettings> settings;
  private final String zonesScript;
  private final Set<DNSManagedZone> zones;

  public DNSServerFacet(
      MachineBuilderContext builderContext,
      MachineShellConnectionFactory connectionFactory,
      String zonesScript,
      Set<DNSZoneSettings> settings) {
    this.agent = builderContext.getAgent();
    this.connectionFactory = requireNonNull(connectionFactory);
    this.zonesScript = requireNonNull(zonesScript);
    this.settings = unmodifiableSet(settings);
    this.zones = new HashSet<>();
    builderContext.addDisposeTask(this::dispose);
  }

  public void enableZone(DNSZoneName name, Function<DNSZone, Stream<DNSHost>> availableHosts) {
    zones.add(new DNSManagedZone(
        agent,
        connectionFactory,
        zonesScript,
        settings.stream()
            .filter(item -> name.equals(item.getName()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(new StringBuilder()
                .append("Settings for zone ").append(name).append(" are not present")
                .toString())),
        availableHosts));
  }

  public void attach(DNSHost availableHost) {
    zones.forEach(zone -> zone.attach(availableHost));
  }

  public void detach(DNSHostName unavailableHostName) {
    zones.forEach(zone -> zone.detach(unavailableHostName));
  }

  private void dispose() {
    zones.forEach(DNSManagedZone::dispose);
  }
}
