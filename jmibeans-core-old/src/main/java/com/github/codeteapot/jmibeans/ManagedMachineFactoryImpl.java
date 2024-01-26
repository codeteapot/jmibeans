package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineContext;
import com.github.codeteapot.jmibeans.machine.MachineFacet;
import com.github.codeteapot.jmibeans.machine.UnknownUserException;
import com.github.codeteapot.jmibeans.port.MachineSessionHostResolutionException;
import com.github.codeteapot.jmibeans.session.MachineSession;
import java.util.HashSet;
import java.util.Set;

class ManagedMachineFactoryImpl implements ManagedMachineFactory, MachineBuilderContext {

  private final MachineContext context;
  private final Set<MachineFacet> facets;
  private final ManagedMachineConstructor machineConstructor;

  ManagedMachineFactoryImpl(MachineContext context) {
    this(context, new HashSet<>(), ManagedMachineImpl::new);
  }

  ManagedMachineFactoryImpl(
      MachineContext context,
      Set<MachineFacet> facets,
      ManagedMachineConstructor machineConstructor) {
    this.context = requireNonNull(context);
    this.facets = requireNonNull(facets);
    this.machineConstructor = requireNonNull(machineConstructor);
  }

  @Override
  public void build(MachineBuilder builder) throws MachineBuildingException, InterruptedException {
    facets.clear();
    builder.build(this);
  }

  @Override
  public ManagedMachine getMachine(MachineSessionPoolReleaser sessionPoolReleaser) {
    return machineConstructor.construct(sessionPoolReleaser, facets);
  }

  @Override
  public MachineSession getSession(String username)
      throws UnknownUserException, MachineSessionHostResolutionException {
    return context.getSession(username);
  }

  @Override
  public void register(MachineFacetFactory factory) throws MachineFacetInstantiationException {
    facets.add(factory.getFacet(context));
  }
}
