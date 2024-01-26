package com.github.codeteapot.jmibeans.machine;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import org.junit.jupiter.api.Test;

public class MachineRefTest {

  private static final byte[] ANY_PORT_ID = {};
  private static final byte[] ANY_MACHINE_ID = {};

  private static final byte[] SOME_PORT_ID = {0x01, 0x03};
  private static final byte[] ANOTHER_PORT_ID = {0x02, 0x04};

  private static final byte[] SOME_MACHINE_ID = {0x0a};
  private static final byte[] ANOTHER_MACHINE_ID = {0x0b};

  private static final int SOME_HASH_CODE_BY_MACHINE_ID = 41;

  private static final String SOME_HEXADECIMAL_WITH_COLON_REPARATOR_STRING = "0103:0a";

  @Test
  public void hasPortId() {
    MachineRef machineRef = new MachineRef(SOME_PORT_ID, ANY_MACHINE_ID);

    byte[] portId = machineRef.getPortId();

    assertThat(portId).isEqualTo(SOME_PORT_ID);
  }

  @Test
  public void hasMachineId() {
    MachineRef machineRef = new MachineRef(ANY_PORT_ID, SOME_MACHINE_ID);

    byte[] machineId = machineRef.getMachineId();

    assertThat(machineId).isEqualTo(SOME_MACHINE_ID);
  }

  @Test
  public void hashCodeByMachineId() {
    MachineRef machineRef = new MachineRef(ANY_PORT_ID, SOME_MACHINE_ID);

    int hashCode = machineRef.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_MACHINE_ID);
  }

  @Test
  public void equalsByJavaObjectReference() {
    MachineRef machineRef = new MachineRef(ANY_PORT_ID, ANY_MACHINE_ID);
    MachineRef anotherMachineRef = machineRef;

    boolean equals = machineRef.equals(anotherMachineRef);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalsByMachineIdAndPortId() {
    MachineRef machineRef = new MachineRef(SOME_PORT_ID, SOME_MACHINE_ID);
    MachineRef anotherMachineRef = new MachineRef(SOME_PORT_ID, SOME_MACHINE_ID);

    boolean equals = machineRef.equals(anotherMachineRef);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualsByJavaType() {
    MachineRef machineRef = new MachineRef(ANY_PORT_ID, ANY_MACHINE_ID);
    Object anotherObject = new Object();

    boolean equals = machineRef.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualsByPortId() {
    MachineRef machineRef = new MachineRef(SOME_PORT_ID, SOME_MACHINE_ID);
    MachineRef anotherMachineRef = new MachineRef(ANOTHER_PORT_ID, SOME_MACHINE_ID);

    boolean equals = machineRef.equals(anotherMachineRef);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualsByMachineId() {
    MachineRef machineRef = new MachineRef(SOME_PORT_ID, SOME_MACHINE_ID);
    MachineRef anotherMachineRef = new MachineRef(SOME_PORT_ID, ANOTHER_MACHINE_ID);

    boolean equals = machineRef.equals(anotherMachineRef);

    assertThat(equals).isFalse();
  }

  @Test
  public void toStringHexadecimalWithColonSeparator() {
    MachineRef machineRef = new MachineRef(SOME_PORT_ID, SOME_MACHINE_ID);

    String str = machineRef.toString();

    assertThat(str).isEqualTo(SOME_HEXADECIMAL_WITH_COLON_REPARATOR_STRING);
  }
}
