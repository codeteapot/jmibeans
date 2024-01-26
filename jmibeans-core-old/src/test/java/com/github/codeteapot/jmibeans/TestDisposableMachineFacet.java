package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.machine.Disposable;
import com.github.codeteapot.jmibeans.machine.MachineFacet;
import com.github.codeteapot.jmibeans.machine.MachineRef;

class TestDisposableMachineFacet implements MachineFacet, Disposable {

  @Override
  public MachineRef getRef() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void dispose() {
    throw new UnsupportedOperationException();
  }
}
