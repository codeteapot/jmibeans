package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MachineShellPasswordNameTest {

  private static final String ANY_VALUE = "any-value";

  private static final String SOME_VALUE = "some-value";
  private static final int SOME_HASH_CODE = 1943409496;

  private static final String ANOTHER_VALUE = "another-value";

  @Test
  void hasValue() {
    MachineShellPasswordName name = new MachineShellPasswordName(SOME_VALUE);

    String value = name.getValue();

    assertThat(value).isEqualTo(SOME_VALUE);
  }

  @Test
  void hashCodeBasedOnValue() {
    MachineShellPasswordName name = new MachineShellPasswordName(SOME_VALUE);

    int hashCode = name.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE);
  }

  @Test
  void equalsByJavaRef() {
    MachineShellPasswordName name = new MachineShellPasswordName(ANY_VALUE);
    MachineShellPasswordName another = name;

    boolean equals = name.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void equalsByValue() {
    MachineShellPasswordName name = new MachineShellPasswordName(SOME_VALUE);
    MachineShellPasswordName another = new MachineShellPasswordName(SOME_VALUE);

    boolean equals = name.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void notEqualByValue() {
    MachineShellPasswordName name = new MachineShellPasswordName(SOME_VALUE);
    MachineShellPasswordName another = new MachineShellPasswordName(ANOTHER_VALUE);

    boolean equals = name.equals(another);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualByJavaType() {
    MachineShellPasswordName name = new MachineShellPasswordName(ANY_VALUE);
    Object another = new Object();

    boolean equals = name.equals(another);

    assertThat(equals).isFalse();
  }

  @Test
  void stringRepresentationIsValue() {
    MachineShellPasswordName name = new MachineShellPasswordName(SOME_VALUE);

    String str = name.toString();

    assertThat(str).isEqualTo(SOME_VALUE);
  }
}
