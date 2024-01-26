package com.github.codeteapot.jmibeans.shell.client.security.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MachineShellIdentityNameTest {

  private static final String ANY_VALUE = "any-value";

  private static final String SOME_VALUE = "some-value";
  private static final int SOME_HASH_CODE = 1943409496;

  private static final String ANOTHER_VALUE = "another-value";

  @Test
  void hasValue() {
    MachineShellIdentityName name = new MachineShellIdentityName(SOME_VALUE);

    String value = name.getValue();

    assertThat(value).isEqualTo(SOME_VALUE);
  }

  @Test
  void hashCodeBasedOnValue() {
    MachineShellIdentityName name = new MachineShellIdentityName(SOME_VALUE);

    int hashCode = name.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE);
  }

  @Test
  void equalsByJavaRef() {
    MachineShellIdentityName name = new MachineShellIdentityName(ANY_VALUE);
    MachineShellIdentityName another = name;

    boolean equals = name.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void equalsByValue() {
    MachineShellIdentityName name = new MachineShellIdentityName(SOME_VALUE);
    MachineShellIdentityName another = new MachineShellIdentityName(SOME_VALUE);

    boolean equals = name.equals(another);

    assertThat(equals).isTrue();
  }

  @Test
  void notEqualByValue() {
    MachineShellIdentityName name = new MachineShellIdentityName(SOME_VALUE);
    MachineShellIdentityName another = new MachineShellIdentityName(ANOTHER_VALUE);

    boolean equals = name.equals(another);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualByJavaType() {
    MachineShellIdentityName name = new MachineShellIdentityName(ANY_VALUE);
    Object another = new Object();

    boolean equals = name.equals(another);

    assertThat(equals).isFalse();
  }

  @Test
  void stringRepresentationIsValue() {
    MachineShellIdentityName name = new MachineShellIdentityName(SOME_VALUE);

    String str = name.toString();

    assertThat(str).isEqualTo(SOME_VALUE);
  }
}
