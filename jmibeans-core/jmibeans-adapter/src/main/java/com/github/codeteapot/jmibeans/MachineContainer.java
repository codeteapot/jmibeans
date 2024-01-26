package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.profile.MachineProfile;

class MachineContainer implements PlatformContext {

  private static final Logger logger = getLogger(MachineContainer.class.getName());

  private final PlatformEventTarget eventTarget;
  private final MachineCatalog catalog;
  private final ExecutorService builderExecutor;
  private final ConcurrentMap<MachineRef, ManagedMachine> machineMap;
  private final ManagedMachineConstructor machineConstructor;

  MachineContainer(
      PlatformEventTarget eventTarget,
      MachineCatalog catalog,
      ExecutorService builderExecutor) {
    this(eventTarget, catalog, builderExecutor, new ConcurrentHashMap<>(), ManagedMachine::new);
  }

  MachineContainer(
      PlatformEventTarget eventTarget,
      MachineCatalog catalog,
      ExecutorService builderExecutor,
      ConcurrentMap<MachineRef, ManagedMachine> machineMap,
      ManagedMachineConstructor machineConstructor) {
    this.eventTarget = requireNonNull(eventTarget);
    this.catalog = requireNonNull(catalog);
    this.builderExecutor = requireNonNull(builderExecutor);
    this.machineMap = requireNonNull(machineMap);
    this.machineConstructor = requireNonNull(machineConstructor);
  }

  @Override
  public Stream<Machine> available() {
    return machineMap.values()
        .stream()
        .filter(ManagedMachine::isReady)
        .collect(toSet())
        .stream()
        .map(Machine.class::cast);
  }

  @Override
  public Optional<Machine> lookup(MachineRef ref) {
    return ofNullable(machineMap.get(ref))
        .filter(ManagedMachine::isReady)
        .map(Machine.class::cast);
  }

  void accept(MachineRef ref, MachineLink link) {
    catalog.getProfile(link.getProfileName())
        .map(this::withProfile)
        .orElseGet(this::withoutProfile)
        .accept(ref, link.getAgent());
    // TODO Catch runtime exception
  }

  void forget(MachineRef ref) {
    ofNullable(machineMap.remove(ref))
        .ifPresent(ManagedMachine::dispose);
  }

  private BiConsumer<MachineRef, MachineAgent> withProfile(MachineProfile profile) {
    return (ref, agent) -> machineMap.computeIfAbsent(
        ref,
        newRef -> machineConstructor.construct(newRef, eventTarget)).build(
            machineMap::remove, // Covered on PlatformAdapterAcceptanceTest
            builderExecutor,
            profile.getBuilder(),
            agent);
  }

  private BiConsumer<MachineRef, MachineAgent> withoutProfile() {
    return (ref, agent) -> logger.warning(new StringBuilder()
        .append("Could not determine profile for machine ").append(ref)
        .toString());
  }
}
