package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.MachineFacetFactory;
import com.github.codeteapot.jmibeans.MachineFacetInstantiationException;
import com.github.codeteapot.jmibeans.machine.MachineContext;
import com.github.codeteapot.jmibeans.machine.MachineFacet;

class TestSayMessageMachineFacetFactory implements MachineFacetFactory {

  @Override
  public MachineFacet getFacet(MachineContext context) throws MachineFacetInstantiationException {
    return new TestSayMessageMachineFacet(context);
  }

}
