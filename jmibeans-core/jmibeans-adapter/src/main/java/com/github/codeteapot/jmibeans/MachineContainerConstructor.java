package com.github.codeteapot.jmibeans;

import java.util.concurrent.ExecutorService;

@FunctionalInterface
interface MachineContainerConstructor {

  MachineContainer construct(
      PlatformEventTarget eventTarget,
      MachineCatalog catalog,
      ExecutorService builderExecutor);
}
