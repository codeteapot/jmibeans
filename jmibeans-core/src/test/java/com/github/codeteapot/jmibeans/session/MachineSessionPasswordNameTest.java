package com.github.codeteapot.jmibeans.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.session.MachineSessionPasswordName;
import org.junit.jupiter.api.Test;

public class MachineSessionPasswordNameTest {

  private static final String ANY_VALUE = "any-value";

  private static final String SOME_VALUE = "some-value";
  private static final String ANOTHER_VALUE = "another-value";

  private static final int SOME_HASH_CODE_BY_VALUE = 1943409496;

  @Test
  public void hasValue() {
    MachineSessionPasswordName passwordName = new MachineSessionPasswordName(SOME_VALUE);

    String value = passwordName.getValue();

    assertThat(value).isEqualTo(SOME_VALUE);
  }

  @Test
  public void hashCodeByValue() {
    MachineSessionPasswordName passwordName = new MachineSessionPasswordName(SOME_VALUE);

    int hashCode = passwordName.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_VALUE);
  }

  @Test
  public void equalsByJavaObjectReference() {
    MachineSessionPasswordName passwordName = new MachineSessionPasswordName(ANY_VALUE);
    MachineSessionPasswordName anotherPasswordName = passwordName;

    boolean equals = passwordName.equals(anotherPasswordName);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalsByValue() {
    MachineSessionPasswordName passwordName = new MachineSessionPasswordName(SOME_VALUE);
    MachineSessionPasswordName anotherPasswordName = new MachineSessionPasswordName(SOME_VALUE);

    boolean equals = passwordName.equals(anotherPasswordName);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualsByJavaType() {
    MachineSessionPasswordName passwordName = new MachineSessionPasswordName(ANY_VALUE);
    Object anotherObject = new Object();

    boolean equals = passwordName.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualsByValue() {
    MachineSessionPasswordName passwordName = new MachineSessionPasswordName(SOME_VALUE);
    MachineSessionPasswordName anotherPasswordName = new MachineSessionPasswordName(ANOTHER_VALUE);

    boolean equals = passwordName.equals(anotherPasswordName);

    assertThat(equals).isFalse();
  }

  @Test
  public void toStringValueItself() {
    MachineSessionPasswordName passwordName = new MachineSessionPasswordName(SOME_VALUE);

    String str = passwordName.toString();

    assertThat(str).isEqualTo(SOME_VALUE);
  }
}
