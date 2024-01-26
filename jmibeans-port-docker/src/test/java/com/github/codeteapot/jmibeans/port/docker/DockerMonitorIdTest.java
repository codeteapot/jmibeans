package com.github.codeteapot.jmibeans.port.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DockerMonitorIdTest {

  private static final String ANY_CONTAINER_ID = "0102";

  private static final String SOME_CONTAINER_ID = "704f";
  private static final byte[] SOME_MACHINE_ID = {0x70, 0x4f};

  private static final String ANOTHER_CONTAINER_ID = "4a6d";

  private static final int SOME_HASH_CODE_BY_MACHINE_ID = 4512;

  private static final String SOME_HEXADECIMAL_STRING = "704f";

  @Test
  void hashCodeByMachineId() {
    DockerMonitorId monitorId = new DockerMonitorId(SOME_CONTAINER_ID);

    int hashCode = monitorId.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_MACHINE_ID);
  }

  @Test
  void equalsByJavaObjectReference() {
    DockerMonitorId monitorId = new DockerMonitorId(ANY_CONTAINER_ID);
    DockerMonitorId anotherMonitorId = monitorId;

    boolean equals = monitorId.equals(anotherMonitorId);

    assertThat(equals).isTrue();
  }

  @Test
  void equalsByMachineId() {
    DockerMonitorId monitorId = new DockerMonitorId(SOME_CONTAINER_ID);
    DockerMonitorId anotherMonitorId = new DockerMonitorId(SOME_CONTAINER_ID);

    boolean equals = monitorId.equals(anotherMonitorId);

    assertThat(equals).isTrue();
  }

  @Test
  void notEqualsByJavaType() {
    DockerMonitorId monitorId = new DockerMonitorId(SOME_CONTAINER_ID);
    Object anotherObject = new Object();

    boolean equals = monitorId.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualsByMachineId() {
    DockerMonitorId monitorId = new DockerMonitorId(SOME_CONTAINER_ID);
    DockerMonitorId anotherMonitorId = new DockerMonitorId(ANOTHER_CONTAINER_ID);

    boolean equals = monitorId.equals(anotherMonitorId);

    assertThat(equals).isFalse();
  }

  @Test
  void hasMachineId() {
    DockerMonitorId monitorId = new DockerMonitorId(SOME_CONTAINER_ID);

    byte[] machineId = monitorId.getMachineId();

    assertThat(machineId).isEqualTo(SOME_MACHINE_ID);
  }

  @Test
  void toStringHexadecimal() {
    DockerMonitorId monitorId = new DockerMonitorId(SOME_CONTAINER_ID);

    String str = monitorId.toString();

    assertThat(str).isEqualTo(SOME_HEXADECIMAL_STRING);
  }
}
