package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.session.MachineSessionFactory;

@FunctionalInterface
interface MachineContainerConstructor {

  MachineContainer construct(
      PlatformEventSource eventSource,
      MachineCatalog catalog,
      MachineSessionFactory sessionFactory);
}
