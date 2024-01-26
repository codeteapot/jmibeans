package com.github.codeteapot.jmibeans.platform;

import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Arrays;

public class MachineRef implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final char[] HEX_ARRAY = {
      '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
  };

  private final byte[] portId;

  private final byte[] machineId;

  @ConstructorProperties({
      "portId",
      "machineId"
  })
  public MachineRef(byte[] portId, byte[] machineId) {
    this.portId = requireNonNull(portId);
    this.machineId = requireNonNull(machineId);
  }

  public byte[] getPortId() {
    return portId;
  }

  public byte[] getMachineId() {
    return machineId;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(machineId);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof MachineRef) {
      MachineRef ref = (MachineRef) obj;
      return Arrays.equals(portId, ref.portId) && Arrays.equals(machineId, ref.machineId);
    }
    return false;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append(bytesToHex(portId))
        .append(':')
        .append(bytesToHex(machineId))
        .toString();
  }

  private static String bytesToHex(byte[] bytes) {
    char[] chars = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      int v = bytes[i] & 0xff;
      chars[i * 2] = HEX_ARRAY[v >>> 4];
      chars[i * 2 + 1] = HEX_ARRAY[v & 0x0f];
    }
    return new String(chars);
  }
}
