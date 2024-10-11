package com.github.codeteapot.jmibeans.library.dns.catalog;

import static java.util.stream.Collectors.toSet;

import com.github.codeteapot.jmibeans.machine.MachineNetworkAddressBinding;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DNSHostFacetFactory {

  public DNSHostFacet getFacet(MachineBuilderContext builderContext, Set<DNSHostConfig> hosts) {
    Map<MachineNetworkName, MachineNetworkAddressBinding> addressBindingMap = new HashMap<>();
    return new DNSHostFacet(hosts.stream()
        .map(host -> host.getHost(addressBindingMap, builderContext))
        .collect(toSet()));
  }
}
