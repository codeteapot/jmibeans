package com.github.codeteapot.jmibeans.port;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MachineProfileNameTest {

  private static final String ANY_VALUE = "any-value";

  private static final String SOME_VALUE = "some-value";
  private static final int SOME_HASH_CODE = 1943409496;

  private static final String ANOTHER_VALUE = "another-value";

  @Test
  void hasValue() {
    MachineProfileName name = new MachineProfileName(SOME_VALUE);

    String value = name.getValue();

    assertThat(value).isEqualTo(SOME_VALUE);
  }

  @Test
  void hashCodeBasedOnValue() {
    MachineProfileName name = new MachineProfileName(SOME_VALUE);

    int hashCode = name.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE);
  }

  @Test
  void equalsByJavaRef() {
    MachineProfileName name = new MachineProfileName(ANY_VALUE);
    MachineProfileName another = name;

    boolean equals = name.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void equalsByValue() {
    MachineProfileName name = new MachineProfileName(SOME_VALUE);
    MachineProfileName another = new MachineProfileName(SOME_VALUE);

    boolean equals = name.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void notEqualByValue() {
    MachineProfileName name = new MachineProfileName(SOME_VALUE);
    MachineProfileName another = new MachineProfileName(ANOTHER_VALUE);

    boolean equals = name.equals(another);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualByJavaType() {
    MachineProfileName name = new MachineProfileName(ANY_VALUE);
    Object another = new Object();

    boolean equals = name.equals(another);

    assertThat(equals).isFalse();
  }

  @Test
  void stringRepresentationIsValue() {
    MachineProfileName name = new MachineProfileName(SOME_VALUE);

    String str = name.toString();

    assertThat(str).isEqualTo(SOME_VALUE);
  }
}
