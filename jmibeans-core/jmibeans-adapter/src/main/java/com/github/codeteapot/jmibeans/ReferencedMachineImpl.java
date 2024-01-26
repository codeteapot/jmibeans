package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.Machine;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.ReferencedMachine;
import java.util.Optional;

class ReferencedMachineImpl implements ReferencedMachine {

  private final MachineRef ref;
  private final Machine object;

  ReferencedMachineImpl(MachineRef ref, Machine object) {
    this.ref = requireNonNull(ref);
    this.object = requireNonNull(object);
  }

  @Override
  public MachineRef getRef() {
    return ref;
  }

  @Override
  public <F> Optional<F> getFacet(Class<F> type) {
    return object.getFacet(type);
  }
}
