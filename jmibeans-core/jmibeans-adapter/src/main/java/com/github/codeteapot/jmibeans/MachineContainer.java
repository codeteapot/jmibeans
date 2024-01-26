package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.toSet;

import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.platform.ReferencedMachine;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.profile.MachineProfile;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

class MachineContainer implements PlatformContext {

  private static final Logger logger = getLogger(MachineContainer.class.getName());

  private final PlatformEventTarget eventTarget;
  private final MachineCatalog catalog;
  private final ExecutorService builderExecutor;
  private final ConcurrentMap<MachineRef, ManagedMachine> machineMap;
  private final ManagedMachineConstructor machineConstructor;
  private final ManagedMachineBuildingJobConstructor buildingJobConstructor;

  MachineContainer(
      PlatformEventTarget eventTarget,
      MachineCatalog catalog,
      ExecutorService builderExecutor) {
    this(
        eventTarget,
        catalog,
        builderExecutor,
        new ConcurrentHashMap<>(),
        ManagedMachine::new,
        ManagedMachineBuildingJob::new);
  }

  MachineContainer(
      PlatformEventTarget eventTarget,
      MachineCatalog catalog,
      ExecutorService builderExecutor,
      ConcurrentMap<MachineRef, ManagedMachine> machineMap,
      ManagedMachineConstructor machineConstructor,
      ManagedMachineBuildingJobConstructor buildingJobConstructor) {
    this.eventTarget = requireNonNull(eventTarget);
    this.catalog = requireNonNull(catalog);
    this.builderExecutor = requireNonNull(builderExecutor);
    this.machineMap = requireNonNull(machineMap);
    this.machineConstructor = requireNonNull(machineConstructor);
    this.buildingJobConstructor = requireNonNull(buildingJobConstructor);
  }

  @Override
  public Stream<ReferencedMachine> available() {
    return machineMap.values()
        .stream()
        .filter(ManagedMachine::isReady)
        .collect(toSet())
        .stream()
        .map(ManagedMachine::referenced);
  }

  @Override
  public Optional<Machine> lookup(MachineRef ref) {
    return ofNullable(machineMap.get(ref))
        .filter(ManagedMachine::isReady)
        .map(Machine.class::cast);
  }

  void accept(MachineRef ref, MachineLink link) {
    try {
      catalog.getProfile(link.getProfileName())
          .map(this::withProfile)
          .orElseGet(this::withoutProfile)
          .accept(new MachineContainerProfileSpecs(
              ref,
              link.getBuilderPropertyResolver(),
              link.getAgent()));
    } catch (RuntimeException e) {
      logger.log(WARNING, new StringBuilder()
          .append("Error occurred while accepting machine ").append(ref)
          .toString(), e);
    }
  }

  void forget(MachineRef ref) {
    try {
      ofNullable(machineMap.remove(ref)).ifPresent(ManagedMachine::dispose);
    } catch (RuntimeException e) {
      logger.log(WARNING, new StringBuilder()
          .append("Error occurred while forgetting machine ").append(ref)
          .toString(), e);
    }
  }

  void forgetAll(Predicate<MachineRef> matcher) {
    machineMap.entrySet().removeIf(entry -> {
      if (matcher.test(entry.getKey())) {
        entry.getValue().dispose();
        return true;
      }
      return false;
    });
  }

  private Consumer<MachineContainerProfileSpecs> withProfile(MachineProfile profile) {
    return specs -> machineMap.computeIfAbsent(specs.ref, newRef -> machineConstructor.construct(
        newRef,
        eventTarget,
        specs.builderPropertyResolver,
        specs.agent,
        buildingJobConstructor.construct(builderExecutor, profile.getBuilder())));
  }

  private Consumer<MachineContainerProfileSpecs> withoutProfile() {
    return specs -> logger.warning(new StringBuilder()
        .append("Could not determine profile for machine ").append(specs.ref)
        .toString());
  }
}
