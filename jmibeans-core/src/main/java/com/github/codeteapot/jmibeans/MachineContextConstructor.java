package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineContext;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineNetworkName;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;

@FunctionalInterface
interface MachineContextConstructor {

  MachineContext construct(
      MachineRef ref,
      MachineRealm realm,
      MachineLink link,
      MachineNetworkName networkName,
      Integer sessionPort,
      MachineSessionFactory sessionFactory);
}
