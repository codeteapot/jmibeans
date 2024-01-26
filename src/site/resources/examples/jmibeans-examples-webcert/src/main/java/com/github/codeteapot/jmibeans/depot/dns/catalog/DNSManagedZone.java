package com.github.codeteapot.jmibeans.depot.dns.catalog;

import static com.github.codeteapot.jmibeans.profile.MachineNetworkBinding.receiveAddressOrNull;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineNetworkBinding;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

class DNSManagedZone implements DNSZone {

  private static final Logger logger = getLogger(DNSZone.class.getName());

  private final MachineShellConnectionFactory connectionFactory;
  private final String zonesScript;
  private final DNSZoneName name;
  private final MachineNetworkName networkName;
  private final MachineNetworkBinding networkBinding;
  private final Function<DNSZone, Stream<DNSHost>> availableHosts;
  private final Map<DNSHostName, MachineNetworkBinding> attachedHosts;
  private DNSService service;

  DNSManagedZone(
      MachineAgent agent,
      MachineShellConnectionFactory connectionFactory,
      String zonesScript,
      DNSZoneSettings settings,
      Function<DNSZone, Stream<DNSHost>> availableHosts) {
    this.connectionFactory = requireNonNull(connectionFactory);
    this.zonesScript = requireNonNull(zonesScript);
    this.name = settings.getName();
    this.networkName = settings.getNetworkName();
    this.availableHosts = requireNonNull(availableHosts);
    attachedHosts = new HashMap<>();
    service = null;
    networkBinding = new MachineNetworkBinding(agent, settings.getNetworkName(), this::setNetwork);
  }

  @Override
  public DNSZoneName getName() {
    return name;
  }

  void attach(DNSHost availableHost) {
    if (attachedHosts.containsKey(availableHost.getName())) {
      // TODO Log warning already attached
    } else {
      attachedHosts.put(availableHost.getName(), availableHost.networkBind(
          networkName,
          receiveAddressOrNull(address -> setAttachedHostAddress(
              availableHost.getName(),
              address))));
    }
  }

  void detach(DNSHostName unavailableHostName) {
    try {
      if (service != null) {
        if (attachedHosts.containsKey(unavailableHostName)) {
          attachedHosts.remove(unavailableHostName).unbind();
          service.delete(name, unavailableHostName);
        } else {
          // TODO Log warning not attached
        }
      }
    } catch (DNSServiceException e) {
      // TODO Log error
    }
  }

  void dispose() {
    attachedHosts.values().forEach(MachineNetworkBinding::unbind);
    networkBinding.unbind();
  }

  private void setNetwork(MachineNetwork network) {
    try {
      if (network == null) {
        attachedHosts.values().forEach(MachineNetworkBinding::unbind);
        attachedHosts.clear();
        service = null;
      } else {
        service = new DNSService(network, connectionFactory, zonesScript);
        service.enableZone(name);
        availableHosts.apply(this).forEach(this::attach);
      }
    } catch (DNSServiceException | UnknownHostException e) {
      // TODO Log error
    }
  }

  private void setAttachedHostAddress(DNSHostName hostName, InetAddress address) {
    try {
      if (service != null) {
        if (address == null) {
          service.delete(name, hostName);
        } else {
          service.add(name, hostName, address);
        }
      }
    } catch (DNSServiceException e) {
      logger.log(SEVERE, format("Could not attach host %s to %s", hostName, name), e);
    }
  }
}
