package com.github.codeteapot.jmibeans.platform;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MachineRefTest {

  private static final byte[] ANY_LISTEN_ID = {};
  private static final byte[] ANY_MACHINE_ID = {};

  private static final byte[] SOME_LISTEN_ID = {0x01, 0x03};
  private static final byte[] ANOTHER_LISTEN_ID = {0x02, 0x04};

  private static final byte[] SOME_MACHINE_ID = {0x0a};
  private static final byte[] ANOTHER_MACHINE_ID = {0x0b};

  private static final int SOME_HASH_CODE_BY_MACHINE_ID = 41;

  private static final String SOME_HEXADECIMAL_WITH_COLON_REPARATOR_STRING = "0103:0a";

  @Test
  void hasListenId() {
    MachineRef machineRef = new MachineRef(SOME_LISTEN_ID, ANY_MACHINE_ID);

    byte[] listenId = machineRef.getListenId();

    assertThat(listenId).isEqualTo(SOME_LISTEN_ID);
  }

  @Test
  void hasMachineId() {
    MachineRef machineRef = new MachineRef(ANY_LISTEN_ID, SOME_MACHINE_ID);

    byte[] machineId = machineRef.getMachineId();

    assertThat(machineId).isEqualTo(SOME_MACHINE_ID);
  }

  @Test
  void hashCodeByMachineId() {
    MachineRef machineRef = new MachineRef(ANY_LISTEN_ID, SOME_MACHINE_ID);

    int hashCode = machineRef.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_MACHINE_ID);
  }

  @Test
  void equalsByJavaObjectReference() {
    MachineRef machineRef = new MachineRef(ANY_LISTEN_ID, ANY_MACHINE_ID);
    MachineRef anotherMachineRef = machineRef;

    boolean equals = machineRef.equals(anotherMachineRef);

    assertThat(equals).isTrue();
  }

  @Test
  void equalsByMachineIdAndListenId() {
    MachineRef machineRef = new MachineRef(SOME_LISTEN_ID, SOME_MACHINE_ID);
    MachineRef anotherMachineRef = new MachineRef(SOME_LISTEN_ID, SOME_MACHINE_ID);

    boolean equals = machineRef.equals(anotherMachineRef);

    assertThat(equals).isTrue();
  }

  @Test
  void notEqualsByJavaType() {
    MachineRef machineRef = new MachineRef(ANY_LISTEN_ID, ANY_MACHINE_ID);
    Object anotherObject = new Object();

    boolean equals = machineRef.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualsByListenId() {
    MachineRef machineRef = new MachineRef(SOME_LISTEN_ID, SOME_MACHINE_ID);
    MachineRef anotherMachineRef = new MachineRef(ANOTHER_LISTEN_ID, SOME_MACHINE_ID);

    boolean equals = machineRef.equals(anotherMachineRef);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualsByMachineId() {
    MachineRef machineRef = new MachineRef(SOME_LISTEN_ID, SOME_MACHINE_ID);
    MachineRef anotherMachineRef = new MachineRef(SOME_LISTEN_ID, ANOTHER_MACHINE_ID);

    boolean equals = machineRef.equals(anotherMachineRef);

    assertThat(equals).isFalse();
  }

  @Test
  void toStringHexadecimalWithColonSeparator() {
    MachineRef machineRef = new MachineRef(SOME_LISTEN_ID, SOME_MACHINE_ID);

    String str = machineRef.toString();

    assertThat(str).isEqualTo(SOME_HEXADECIMAL_WITH_COLON_REPARATOR_STRING);
  }
}
