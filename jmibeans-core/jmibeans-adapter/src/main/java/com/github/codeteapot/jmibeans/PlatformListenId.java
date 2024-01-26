package com.github.codeteapot.jmibeans;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Function;

import com.github.codeteapot.jmibeans.platform.MachineRef;

class PlatformListenId {

  private static final int VALUE_BOUND = Integer.MAX_VALUE;

  private final byte[] value;

  PlatformListenId(Function<Integer, Integer> randomInt) {
    value = BigInteger.valueOf(randomInt.apply(VALUE_BOUND)).toByteArray();
  }

  boolean isParentOf(MachineRef machineRef) {
    return Arrays.equals(value, machineRef.getListenId());
  }

  MachineRef machineRef(byte[] machineId) {
    return new MachineRef(value, machineId);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof PlatformListenId) {
      PlatformListenId id = (PlatformListenId) obj;
      return Arrays.equals(value, id.value);
    }
    return false;
  }
}
