package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import java.util.concurrent.ExecutorService;

/**
 * Adapter between a Java Beans application and the mutable infrastructure.
 *
 * <p>Changes in the availability of infrastructure machines are known through the provided
 * {@link PlatformEventTarget}. The provided {@link MachineCatalog} is responsible for defining the
 * profile of these machines.
 *
 * <p>The application has consistent access to available machines across all listened
 * {@link PlatformPort}s using the {@link PlatformContext} offered by the adapter.
 */
public class PlatformAdapter {

  private final PlatformListenIdGenerator listenIdGenerator;
  private final MachineContainer container;

  /**
   * Adapter that uses the specified event target and catalog.
   *
   * <p>Initially, the adapter is not listening to any port. To start listening on a port, the
   * {@link #listen(PlatformPort)} operation must be called, passing the instance of that port.
   *
   * <p>Building a machine can be a blocking operation, so the executor of the task that involves it
   * is specified.
   *
   * @param eventTarget Target through which the events resulting from port listening are fired.
   * @param catalog Catalog that defines the profiles of the machines available on the platform.
   * @param builderExecutor Executor of the building tasks of the machines on the platform.
   */
  public PlatformAdapter(
      PlatformEventTarget eventTarget,
      MachineCatalog catalog,
      ExecutorService builderExecutor) {
    this(
        eventTarget,
        catalog,
        builderExecutor,
        new PlatformListenIdGenerator(),
        MachineContainer::new);
  }

  PlatformAdapter(
      PlatformEventTarget eventTarget,
      MachineCatalog catalog,
      ExecutorService builderExecutor,
      PlatformListenIdGenerator listenIdGenerator,
      MachineContainerConstructor containerConstructor) {
    this.listenIdGenerator = requireNonNull(listenIdGenerator);
    container = containerConstructor.construct(eventTarget, catalog, builderExecutor);
  }

  /**
   * Consistent access to machines across the infrastructure being listened to.
   *
   * @return The platform context that gives consistent access to all machines.
   */
  public PlatformContext getContext() {
    return container;
  }

  /**
   * Performs listening on the specified port.
   *
   * <p>This is a blocking operation. It ends when the thread responsible for listening to the port
   * is interrupted. A new thread must be used so that the adapter keeps listening on more than one
   * port.
   *
   * <p>The possibility of listening to the same instance more than once is left to the port
   * implementation.
   *
   * <p>When listening to a port ends, the machines that would have been made available to the
   * platform context by the port are lost.
   *
   * @param port The port that is responsible for the infrastructure that will be listened to.
   *
   * @throws InterruptedException When listening to the port is interrupted.
   */
  public void listen(PlatformPort port) throws InterruptedException {
    try (PlatformAdapterMachineManager manager = new PlatformAdapterMachineManager(
        listenIdGenerator,
        container)) {
      port.listen(manager);
    }
  }
}
