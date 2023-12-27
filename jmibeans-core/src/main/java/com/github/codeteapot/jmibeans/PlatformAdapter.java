package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;

/**
 * Adapter to interact with the platform by reacting to the events that occur on it.
 *
 * <p>The access to the platform is abstracted through the definition of a catalog and the
 * implementation of a session factory for the machines involved.
 */
public class PlatformAdapter {

  private final PlatformPortIdGenerator portIdGenerator;
  private final MachineContainer container;

  /**
   * Creates an adapter given the platform event source, and the catalog and session factory of
   * machines.
   *
   * @param eventSource Source of events that occur on the platform.
   * @param catalog Catalog used to create the machine instances.
   * @param sessionFactory Implementation of the session factory for the machines.
   */
  public PlatformAdapter(
      PlatformEventSource eventSource,
      MachineCatalog catalog,
      MachineSessionFactory sessionFactory) {
    this(
        eventSource,
        catalog,
        sessionFactory,
        new PlatformPortIdGenerator(),
        MachineContainer::new);
  }

  PlatformAdapter(
      PlatformEventSource eventSource,
      MachineCatalog catalog,
      MachineSessionFactory sessionFactory,
      PlatformPortIdGenerator portIdGenerator,
      MachineContainerConstructor containerConstructor) {
    this.portIdGenerator = requireNonNull(portIdGenerator);
    container = containerConstructor.construct(eventSource, catalog, sessionFactory);
  }

  /**
   * The platform context generated by this adapter.
   *
   * @return The generated platform context.
   */
  public PlatformContext getContext() {
    return container;
  }

  /**
   * Keeps the current thread watching for infrastructure changes via the corresponding platform
   * port.
   * 
   * <p>This is a blocking operation. Different ports can be listened on in different threads.
   *
   * @param port The platform port through which the target infrastructure is watched.
   *
   * @throws InterruptedException In case of interrupting the thread in which the operation is
   *         executed.
   */
  public void listen(PlatformPort port) throws InterruptedException {
    port.listen(new PlatformAdapterMachineManager(portIdGenerator, container));
  }
}