package com.github.codeteapot.jmibeans;

import static java.math.BigInteger.valueOf;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineId;
import java.util.Random;

class PlatformPortId {

  private static final int VALUE_BOUND = Short.MAX_VALUE;

  private final int value;

  PlatformPortId(Random random) {
    value = random.nextInt(VALUE_BOUND);
  }

  MachineRef machineRef(MachineId id) {
    return id.machineRef(valueOf(value).toByteArray());
  }

  @Override
  public int hashCode() {
    return value % 20;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof PlatformPortId) {
      PlatformPortId id = (PlatformPortId) obj;
      return value == id.value;
    }
    return false;
  }
}
