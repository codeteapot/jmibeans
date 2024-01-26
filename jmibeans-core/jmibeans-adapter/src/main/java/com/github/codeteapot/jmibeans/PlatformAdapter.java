package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import java.util.concurrent.ExecutorService;

public class PlatformAdapter {

  private final PlatformPortIdGenerator portIdGenerator;
  private final MachineContainer container;

  public PlatformAdapter(
      PlatformEventTarget eventTarget,
      MachineCatalog catalog,
      ExecutorService builderExecutor) {
    this(
        eventTarget,
        catalog,
        builderExecutor,
        new PlatformPortIdGenerator(),
        MachineContainer::new);
  }

  PlatformAdapter(
      PlatformEventTarget eventTarget,
      MachineCatalog catalog,
      ExecutorService builderExecutor,
      PlatformPortIdGenerator portIdGenerator,
      MachineContainerConstructor containerConstructor) {
    this.portIdGenerator = requireNonNull(portIdGenerator);
    container = containerConstructor.construct(eventTarget, catalog, builderExecutor);
  }

  public PlatformContext getContext() {
    return container;
  }

  public void listen(PlatformPort port) throws InterruptedException {
    port.listen(new PlatformAdapterMachineManager(portIdGenerator, container));
  }
}
