package com.github.codeteapot.jmibeans.port;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.codeteapot.jmibeans.machine.MachineNetworkName;

class AbstractMachineNetworkTest {

  private static final MachineNetworkName ANY_NAME = new MachineNetworkName("any-network");

  private static final MachineNetworkName SOME_NAME = new MachineNetworkName("some-network");
  private static final MachineNetworkName ANOTHER_NAME = new MachineNetworkName("another-network");

  private static final int SOME_HASH_CODE = 917634645;

  @Test
  void hasName() {
    AbstractMachineNetwork network = new TestMachineNetwork(SOME_NAME);

    MachineNetworkName name = network.getName();

    assertThat(name).isEqualTo(SOME_NAME);
  }

  @Test
  void hashCodeBasedOnName() {
    AbstractMachineNetwork network = new TestMachineNetwork(SOME_NAME);

    int hashCode = network.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE);
  }

  @Test
  void equalsByJavaRef() {
    AbstractMachineNetwork network = new TestMachineNetwork(ANY_NAME);
    AbstractMachineNetwork another = network;

    boolean equals = network.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void equalsByName() {
    AbstractMachineNetwork network = new TestMachineNetwork(SOME_NAME);
    AbstractMachineNetwork another = new TestMachineNetwork(SOME_NAME);

    boolean equals = network.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void notEqualByName() {
    AbstractMachineNetwork network = new TestMachineNetwork(SOME_NAME);
    AbstractMachineNetwork another = new TestMachineNetwork(ANOTHER_NAME);

    boolean equals = network.equals(another);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualByJavaType() {
    AbstractMachineNetwork network = new TestMachineNetwork(ANY_NAME);
    Object another = new Object();

    boolean equals = network.equals(another);

    assertThat(equals).isFalse();
  }
}
