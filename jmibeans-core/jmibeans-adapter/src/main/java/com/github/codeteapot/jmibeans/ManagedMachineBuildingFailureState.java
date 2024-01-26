package com.github.codeteapot.jmibeans;

import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import java.util.Optional;
import java.util.logging.Logger;

class ManagedMachineBuildingFailureState extends ManagedMachineState {

  private static final Logger logger = getLogger(ManagedMachine.class.getName());

  ManagedMachineBuildingFailureState(ManagedMachineStateChanger stateChanger, MachineRef ref) {
    super(stateChanger, ref);
  }

  @Override
  <F> Optional<F> getFacet(Class<F> type) {
    throw new IllegalStateException("The machine build has failed");
  }

  @Override
  boolean isReady() {
    return false;
  }

  @Override
  void dispose() {
    logger.fine(new StringBuilder()
        .append("Machine ").append(ref).append(" was not disposed since it was not built")
        .toString());
  }
}
