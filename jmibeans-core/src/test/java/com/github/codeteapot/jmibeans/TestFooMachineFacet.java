package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.MachineFacet;
import com.github.codeteapot.jmibeans.machine.MachineRef;

class TestFooMachineFacet implements MachineFacet {

  @Override
  public MachineRef getRef() {
    throw new UnsupportedOperationException();
  }
}
