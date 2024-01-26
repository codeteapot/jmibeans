package com.github.codeteapot.jmibeans.machine;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.Machine;
import com.github.codeteapot.jmibeans.PlatformAdapter;
import com.github.codeteapot.jmibeans.PlatformContext;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Reference of a {@link Machine} in the platform context.
 *
 * <p>It allows to uniquely identify a machine on the platform.
 * <pre>
 * context.lookup(event.getMachineRef()).ifPresent(machine -&gt; { &#47;* do something *&#47; });
 * </pre>
 *
 * <p>A host reference is the composition of its owning platform port identifier &mdash; generated
 * by the platform adapter &mdash; and the machine identifier determined by the underlying
 * infrastructure of the platform port.
 *
 * @see PlatformContext
 * @see PlatformPort
 * @see PlatformAdapter
 */
public class MachineRef implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

  /**
   * Owning port identifier.
   */
  private final byte[] portId;

  /**
   * Machine identifier in the underlying infrastructure.
   */
  private final byte[] machineId;

  /**
   * Creates a reference given the port identifier and the machine identifier in the underlying
   * infrastructure.
   *
   * @param portId The owning port identifier.
   * @param machineId The machine identifier in the underlying infrastructure.
   */
  @ConstructorProperties({
      "portId",
      "machineId"
  })
  public MachineRef(byte[] portId, byte[] machineId) {
    this.portId = requireNonNull(portId);
    this.machineId = requireNonNull(machineId);
  }

  /**
   * Owning port identifier.
   *
   * @return The port identifier.
   */
  public byte[] getPortId() {
    return portId;
  }

  /**
   * Machine identifier in the underlying infrastructure.
   *
   * @return The machine identifier in the underlying infrastructure.
   */
  public byte[] getMachineId() {
    return machineId;
  }

  /**
   * Based on the machine identifier in the underlying infrastructure.
   */
  @Override
  public int hashCode() {
    return Arrays.hashCode(machineId);
  }

  /**
   * Based on the port identifier and machine identifier in the underlying infrastructure.
   */
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

  /**
   * Port identifier and machine identifier in hexadecimal, separated by a colon.
   */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    appendHex(str, portId)
        .append(':');
    appendHex(str, machineId);
    return str.toString();
  }

  private StringBuilder appendHex(StringBuilder str, byte[] bytes) {
    for (int i = 0; i < bytes.length; ++i) {
      int b = bytes[i] & 0xff;
      str.append(HEX_ARRAY[b >>> 4]).append(HEX_ARRAY[b & 0x0f]);
    }
    return str;
  }
}
