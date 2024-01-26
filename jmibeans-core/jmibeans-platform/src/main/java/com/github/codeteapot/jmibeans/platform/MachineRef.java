package com.github.codeteapot.jmibeans.platform;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.event.MachineEvent;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Reference of a machine on the platform.
 *
 * <p>It is made up of two parts, the identifier generated when listening to the associated port and
 * the machine identifier assigned by the infrastructure provider.
 *
 * @see PlatformContext#lookup(MachineRef)
 * @see MachineEvent#getMachineRef()
 */
public class MachineRef implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final char[] HEX_ARRAY = {
      '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
  };

  /**
   *  Identifier generated when listening to the associated port.
   */
  private final byte[] listenId;

  /**
   * Machine identifier assigned by the infrastructure provider.
   */
  private final byte[] machineId;

  /**
   * Creates a reference with the specified listen and machine identifiers.
   *
   * @param listenId The identifier generated when listening to the associated port.
   * @param machineId The machine identifier assigned by the infrastructure provider.
   */
  @ConstructorProperties({
      "listenId",
      "machineId"
  })
  public MachineRef(byte[] listenId, byte[] machineId) {
    this.listenId = requireNonNull(listenId);
    this.machineId = requireNonNull(machineId);
  }

  /**
   * Identifier generated when listening to the associated port.
   *
   * @return The identifier value.
   */
  public byte[] getListenId() {
    return listenId;
  }

  /**
   * Machine identifier assigned by the infrastructure provider.
   *
   * @return The identifier value.
   */
  public byte[] getMachineId() {
    return machineId;
  }

  /**
   * Hash code based on the machine identifier assigned by the infrastructure provider.
   */
  @Override
  public int hashCode() {
    return Arrays.hashCode(machineId);
  }

  /**
   * Equality based on both identifiers.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof MachineRef) {
      MachineRef ref = (MachineRef) obj;
      return Arrays.equals(listenId, ref.listenId) && Arrays.equals(machineId, ref.machineId);
    }
    return false;
  }

  /**
   * Concatenation of both identifiers in hexadecimal, separated by a colon ({@code :}).
   */
  @Override
  public String toString() {
    return new StringBuilder()
        .append(bytesToHex(listenId))
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
