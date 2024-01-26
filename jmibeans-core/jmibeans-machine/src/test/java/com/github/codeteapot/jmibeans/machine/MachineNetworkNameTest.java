package com.github.codeteapot.jmibeans.machine;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MachineNetworkNameTest {

  private static final String ANY_VALUE = "any-value";

  private static final String SOME_VALUE = "some-value";
  private static final int SOME_HASH_CODE = 1943409496;

  private static final String ANOTHER_VALUE = "another-value";

  @Test
  void hasValue() {
    MachineNetworkName name = new MachineNetworkName(SOME_VALUE);

    String value = name.getValue();

    assertThat(value).isEqualTo(SOME_VALUE);
  }

  @Test
  void hashCodeBasedOnValue() {
    MachineNetworkName name = new MachineNetworkName(SOME_VALUE);

    int hashCode = name.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE);
  }

  @Test
  void equalsByJavaRef() {
    MachineNetworkName name = new MachineNetworkName(ANY_VALUE);
    MachineNetworkName another = name;

    boolean equals = name.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void equalsByValue() {
    MachineNetworkName name = new MachineNetworkName(SOME_VALUE);
    MachineNetworkName another = new MachineNetworkName(SOME_VALUE);

    boolean equals = name.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void notEqualByValue() {
    MachineNetworkName name = new MachineNetworkName(SOME_VALUE);
    MachineNetworkName another = new MachineNetworkName(ANOTHER_VALUE);

    boolean equals = name.equals(another);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualByJavaType() {
    MachineNetworkName name = new MachineNetworkName(ANY_VALUE);
    Object another = new Object();

    boolean equals = name.equals(another);

    assertThat(equals).isFalse();
  }

  @Test
  void stringRepresentationIsValue() {
    MachineNetworkName name = new MachineNetworkName(SOME_VALUE);

    String str = name.toString();

    assertThat(str).isEqualTo(SOME_VALUE);
  }
}
