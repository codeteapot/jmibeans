package com.github.codeteapot.jmibeans.depot.dns.catalog;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineNetworkBinding;
import java.util.function.Consumer;

public interface DNSHost {

  DNSHostName getName();

  MachineNetworkBinding networkBind(
      MachineNetworkName networkName,
      Consumer<MachineNetwork> receiver);
}
