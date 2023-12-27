package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.toSet;

import com.github.codeteapot.jmibeans.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.machine.MachineContext;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.session.MachineSessionFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import java.util.stream.Stream;

class MachineContainer implements PlatformContext {

  private static final Logger logger = getLogger(MachineContainer.class.getName());

  private final PlatformEventSource eventSource;
  private final MachineCatalog catalog;
  private final MachineSessionFactory sessionFactory;
  private final MachineContainerActionPerformer actionPerformer;
  private final Map<MachineRef, ManagedMachine> machineMap;
  private final ScheduledExecutorService sessionPoolReleaseExecutor;
  private final PooledMachineSessionFactoryConstructor pooledSessionFactoryConstructor;
  private final MachineContextConstructor contextConstructor;
  private final ManagedMachineFactoryConstructor managedFactoryConstructor;
  private final MachineAvailableEventConstructor availableEventConstructor;
  private final MachineLostEventConstructor lostEventConstructor;

  MachineContainer(
      PlatformEventSource eventSource,
      MachineCatalog catalog,
      MachineSessionFactory sessionFactory) {
    this(
        eventSource,
        catalog,
        sessionFactory,
        new MachineContainerActionPerformer(),
        new HashMap<>(),
        newSingleThreadScheduledExecutor(),
        PooledMachineSessionFactory::new,
        MachineContextImpl::new,
        ManagedMachineFactoryImpl::new,
        MachineAvailableEvent::new,
        MachineLostEvent::new);
  }

  MachineContainer(
      PlatformEventSource eventSource,
      MachineCatalog catalog,
      MachineSessionFactory sessionFactory,
      MachineContainerActionPerformer actionPerformer,
      Map<MachineRef, ManagedMachine> machineMap,
      ScheduledExecutorService sessionPoolReleaseExecutor,
      PooledMachineSessionFactoryConstructor pooledSessionFactoryConstructor,
      MachineContextConstructor contextConstructor,
      ManagedMachineFactoryConstructor managedFactoryConstructor,
      MachineAvailableEventConstructor availableEventConstructor,
      MachineLostEventConstructor lostEventConstructor) {
    this.eventSource = requireNonNull(eventSource);
    this.catalog = requireNonNull(catalog);
    this.sessionFactory = requireNonNull(sessionFactory);
    this.actionPerformer = requireNonNull(actionPerformer);
    this.machineMap = requireNonNull(machineMap);
    this.sessionPoolReleaseExecutor = requireNonNull(sessionPoolReleaseExecutor);
    this.pooledSessionFactoryConstructor = requireNonNull(pooledSessionFactoryConstructor);
    this.contextConstructor = requireNonNull(contextConstructor);
    this.managedFactoryConstructor = requireNonNull(managedFactoryConstructor);
    this.availableEventConstructor = requireNonNull(availableEventConstructor);
    this.lostEventConstructor = requireNonNull(lostEventConstructor);
  }

  @Override
  public Stream<Machine> available() {
    return machineMap.values()
        .stream()
        .collect(toSet())
        .stream()
        .map(Machine.class::cast);
  }

  @Override
  public Optional<Machine> lookup(MachineRef ref) {
    return ofNullable(machineMap.get(ref));
  }

  void accept(MachineRef ref, MachineLink link) {
    actionPerformer.perform(ref, () -> {
      if (machineMap.containsKey(ref)) {
        logger.warning(new StringBuilder()
            .append("Machine ").append(ref).append(" already exists")
            .toString());
      } else {
        catalog.getProfile(link.getProfileName())
            .map(this::withProfile)
            .orElseGet(this::withoutProfile)
            .accept(ref, link);
      }
    });
  }

  void forget(MachineRef ref) {
    actionPerformer.perform(ref, () -> {
      if (machineMap.containsKey(ref)) {
        machineMap.remove(ref).dispose();
        eventSource.fireEvent(lostEventConstructor.construct(this, ref));
      } else {
        logger.warning(new StringBuilder()
            .append("Machine ").append(ref).append(" does not exist")
            .toString());
      }
    });
  }

  private MachineContainerAcceptClause withProfile(MachineProfile profile) {
    return (ref, link) -> withProfile(ref, link, profile);
  }

  private void withProfile(MachineRef ref, MachineLink link, MachineProfile profile)
      throws InterruptedException {
    try {
      PooledMachineSessionFactory pooledSessionFactory = pooledSessionFactoryConstructor.construct(
          sessionFactory,
          sessionPoolReleaseExecutor,
          ref,
          profile.getSessionPool());
      MachineContext context = contextConstructor.construct(
          ref,
          profile.getRealm(),
          link,
          profile.getNetworkName(),
          profile.getSessionPort().orElse(null),
          pooledSessionFactory);
      ManagedMachineFactory managedFactory = managedFactoryConstructor.construct(context);
      managedFactory.build(profile.getBuilder());
      machineMap.put(ref, managedFactory.getMachine(pooledSessionFactory));
      eventSource.fireEvent(availableEventConstructor.construct(this, ref));
    } catch (MachineBuildingException e) {
      logger.log(SEVERE, "Unable to build machine", e);
    } catch (RuntimeException e) {
      logger.log(SEVERE, "Unable to accept machine", e);
    }
  }

  private MachineContainerAcceptClause withoutProfile() {
    return this::withoutProfile;
  }

  private void withoutProfile(MachineRef ref, MachineLink link) {
    logger.warning(new StringBuilder()
        .append("Could not determine profile for machine ").append(ref)
        .toString());
  }
}
