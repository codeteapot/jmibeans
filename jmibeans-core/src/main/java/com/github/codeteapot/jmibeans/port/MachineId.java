package com.github.codeteapot.jmibeans.port;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import java.util.Arrays;

/**
 * Wrapper for a machine identifier, in the underlying infrastructure, encoded in a byte array.
 *
 * <p>The value of this identifier is defined by the infrastructure. This is always the same on a
 * machine, and must be unique to that machine on the infrastructure to which it belongs.
 */
public class MachineId {

  private static final char[] HEX_ARRAY = {
      '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
  };

  private final byte[] value;

  /**
   * Machine identifier whose encoded value is the specified byte array.
   *
   * @param value The value encoded in byte array.
   */
  public MachineId(byte[] value) {
    this.value = requireNonNull(value);
  }

  /**
   * Composes the machine reference using the encoded value of the platform identifier.
   *
   * @param portId The platform identifier encoded in a byte array.
   *
   * @return The resulting reference to the machine.
   */
  public MachineRef machineRef(byte[] portId) {
    return new MachineRef(portId, value);
  }

  /**
   * Based on value.
   */
  @Override
  public int hashCode() {
    return Arrays.hashCode(value);
  }

  /**
   * Based on value.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof MachineId) {
      MachineId machineId = (MachineId) obj;
      return Arrays.equals(value, machineId.value);
    }
    return false;
  }
  
  /**
   * The machine identifier value itself, in hexadecimal.
   */
  @Override
  public String toString() {
    return bytesToHex(value);
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
