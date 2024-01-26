package com.github.codeteapot.jmibeans.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.session.MachineSessionIdentityName;
import org.junit.jupiter.api.Test;

public class MachineSessionIdentityNameTest {

  private static final String ANY_VALUE = "any-value";

  private static final String SOME_VALUE = "some-value";
  private static final String ANOTHER_VALUE = "another-value";

  private static final int SOME_HASH_CODE_BY_VALUE = 1943409496;

  @Test
  public void hasValue() {
    MachineSessionIdentityName identityName = new MachineSessionIdentityName(SOME_VALUE);

    String value = identityName.getValue();

    assertThat(value).isEqualTo(SOME_VALUE);
  }

  @Test
  public void hashCodeByValue() {
    MachineSessionIdentityName identityName = new MachineSessionIdentityName(SOME_VALUE);

    int hashCode = identityName.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_VALUE);
  }

  @Test
  public void equalsByJavaObjectReference() {
    MachineSessionIdentityName identityName = new MachineSessionIdentityName(ANY_VALUE);
    MachineSessionIdentityName anotherIdentityName = identityName;

    boolean equals = identityName.equals(anotherIdentityName);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalsByValue() {
    MachineSessionIdentityName identityName = new MachineSessionIdentityName(SOME_VALUE);
    MachineSessionIdentityName anotherIdentityName = new MachineSessionIdentityName(SOME_VALUE);

    boolean equals = identityName.equals(anotherIdentityName);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualsByJavaType() {
    MachineSessionIdentityName identityName = new MachineSessionIdentityName(ANY_VALUE);
    Object anotherObject = new Object();

    boolean equals = identityName.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualsByValue() {
    MachineSessionIdentityName identityName = new MachineSessionIdentityName(SOME_VALUE);
    MachineSessionIdentityName anotherIdentityName = new MachineSessionIdentityName(ANOTHER_VALUE);

    boolean equals = identityName.equals(anotherIdentityName);

    assertThat(equals).isFalse();
  }

  @Test
  public void toStringValueItself() {
    MachineSessionIdentityName identityName = new MachineSessionIdentityName(SOME_VALUE);

    String str = identityName.toString();

    assertThat(str).isEqualTo(SOME_VALUE);
  }
}
