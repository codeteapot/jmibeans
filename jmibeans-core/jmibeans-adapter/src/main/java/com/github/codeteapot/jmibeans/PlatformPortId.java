package com.github.codeteapot.jmibeans;

import static java.math.BigInteger.valueOf;

import java.util.function.Function;

import com.github.codeteapot.jmibeans.platform.MachineRef;

class PlatformPortId {

  private static final int VALUE_BOUND = Short.MAX_VALUE;

  private final int value;

  PlatformPortId(Function<Integer, Integer> randomInt) {
    value = randomInt.apply(VALUE_BOUND);
  }

  MachineRef machineRef(byte[] machineId) {
    return new MachineRef(valueOf(value).toByteArray(), machineId);
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
