package com.github.codeteapot.jmibeans.port;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineId;
import org.junit.jupiter.api.Test;

public class MachineIdTest {

  private static final byte[] ANY_VALUE = new byte[0];

  private static final byte[] SOME_VALUE = {0x01, 0x0c, 0x03};
  private static final byte[] SOME_PORT_ID_VALUE = {0x04, 0x05, 0x06};
  private static final byte[] ANOTHER_VALUE = {0x04, 0x0f, 0x07};

  private static final int SOME_HASH_CODE_BY_VALUE = 31127;

  private static final String SOME_VALUE_HEXADECIMAL = "010c03";


  @Test
  public void composeMachineRef() {
    MachineId machineId = new MachineId(SOME_VALUE);

    MachineRef machineRef = machineId.machineRef(SOME_PORT_ID_VALUE);

    assertThat(machineRef.getMachineId()).isEqualTo(SOME_VALUE);
    assertThat(machineRef.getPortId()).isEqualTo(SOME_PORT_ID_VALUE);
  }

  @Test
  public void hashCodeByValue() {
    MachineId machineId = new MachineId(SOME_VALUE);

    int hashCode = machineId.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_VALUE);
  }

  @Test
  public void equalsByJavaObjectReference() {
    MachineId machineId = new MachineId(ANY_VALUE);
    MachineId anotherMachineId = machineId;

    boolean equals = machineId.equals(anotherMachineId);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalsByValue() {
    MachineId machineId = new MachineId(SOME_VALUE);
    MachineId anotherMachineId = new MachineId(SOME_VALUE);

    boolean equals = machineId.equals(anotherMachineId);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualsByJavaType() {
    MachineId machineId = new MachineId(ANY_VALUE);
    Object anotherObject = new Object();

    boolean equals = machineId.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualsByValue() {
    MachineId machineId = new MachineId(SOME_VALUE);
    MachineId anotherMachineId = new MachineId(ANOTHER_VALUE);

    boolean equals = machineId.equals(anotherMachineId);

    assertThat(equals).isFalse();
  }

  @Test
  public void toStringValueItself() {
    MachineId machineId = new MachineId(SOME_VALUE);

    String str = machineId.toString();

    assertThat(str).isEqualTo(SOME_VALUE_HEXADECIMAL);
  }
}
