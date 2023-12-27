package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import java.util.concurrent.ScheduledExecutorService;

@FunctionalInterface
interface PooledMachineSessionFactoryConstructor {

  PooledMachineSessionFactory construct(
      MachineSessionFactory managedSessionFactory,
      ScheduledExecutorService releaseExecutor,
      MachineRef machineRef,
      MachineSessionPool sessionPool);
}
